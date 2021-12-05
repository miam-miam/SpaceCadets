#![feature(proc_macro_span)]
#![feature(proc_macro_span_shrink)]
#![feature(result_into_ok_or_err)]

use proc_macro as pm;
use std::iter::Peekable;

use proc_macro2 as pm2;
use quote::{format_ident, quote, quote_spanned};

/// A simple expression (incr, decr,...)
/// we must use a macro as $quote must be evaluated after the macro has run.
macro_rules! simple_expression {
    ( $argument:ident, $expressions:ident, $errors:ident, $iter:ident, $quote:expr ) => {
        match create_arg($iter) {
            Ok(ok) => {
                $argument = ok;
                match check_for_semi($iter.peek(), $argument.span()) {
                    Ok(_) => {
                        $iter.next();
                        $expressions.push($quote)
                    }
                    Err(e) => $errors.push(e),
                }
            }
            Err(e) => $errors.push(e),
        };
    };
}

#[proc_macro]
pub fn bb(input: pm::TokenStream) -> pm::TokenStream {
    bb_to_rust(&mut input.into_iter().peekable(), None)
        .into_ok_or_err()
        .into()
}

/// A recursive function that is called when a while loop is encountered.
/// We use a result to indicate if any parsing errors when encountered whilst recursing.
fn bb_to_rust(
    iter: &mut Peekable<proc_macro::token_stream::IntoIter>,
    while_span: Option<pm2::Span>,
) -> Result<pm2::TokenStream, pm2::TokenStream> {
    let mut expressions = vec![];
    let mut errors = vec![];
    while let Some(token) = iter.next() {
        match token.to_string().to_lowercase().as_str() {
            "incr" => {
                let argument;
                simple_expression!(
                    argument,
                    expressions,
                    errors,
                    iter,
                    quote_spanned!(argument.span()=> #argument += 1;)
                );
            }
            "decr" => {
                let argument;
                simple_expression!(
                    argument,
                    expressions,
                    errors,
                    iter,
                    quote_spanned!(argument.span()=> #argument -= 1;)
                );
            }
            "clear" => {
                let argument;
                simple_expression!(
                    argument,
                    expressions,
                    errors,
                    iter,
                    quote_spanned!(argument.span()=> let mut #argument = 0;)
                );
            }
            "while" => {
                let mut closure = || -> Result<pm2::TokenStream, pm2::TokenStream> {
                    let argument = create_arg(iter)?;
                    check_for_ident(iter.peek(), argument.span(), "not")?;
                    iter.next();
                    check_for_zero(iter.peek(), argument.span())?;
                    iter.next();
                    check_for_ident(iter.peek(), argument.span(), "do")?;
                    iter.next();
                    check_for_semi(iter.peek(), argument.span())?;
                    iter.next();
                    let inside_expression = bb_to_rust(iter, Some(argument.span()))?;
                    Ok(quote_spanned!(argument.span()=>
                        while #argument != 0 {
                            #inside_expression
                        }
                    ))
                };
                match closure() {
                    Ok(x) => expressions.push(x),
                    Err(x) => errors.push(x),
                }
            }
            "end" if while_span.is_some() => {
                match check_for_semi(iter.peek(), pm2::Span::from(token.span())) {
                    Err(e) => errors.push(e),
                    _ => {
                        iter.next();
                    }
                }
                return if errors.is_empty() {
                    Ok(quote!(#(#expressions)*))
                } else {
                    Err(quote!(#(#errors)*))
                };
            }
            _ => {
                let message = format!("Unexpected expression: \"{}\".", token);
                errors.push(quote_spanned! {
                    pm2::Span::from(token.span())=>
                    compile_error!(#message);
                });
            }
        }
    }
    if let Some(span) = while_span {
        errors.push(quote_spanned! {
            span=>
            compile_error!("Unclosed while loop.");
        });
    }
    if errors.is_empty() {
        Ok(quote!(#(#expressions)*))
    } else {
        Err(quote!(#(#errors)*))
    }
}

/// Create a new variable.
fn create_arg(
    iter: &mut Peekable<proc_macro::token_stream::IntoIter>,
) -> Result<pm2::Ident, pm2::TokenStream> {
    let peek = iter.peek();
    if let Some(ref peek) = peek {
        if peek.to_string() == "'" {
            iter.next();
            let res = Ok(format_ident!("{}", get_ident(iter.peek())?));
            iter.next();
            return res;
        }
    }
    let res = Ok(format_ident!("__bb_{}", get_ident(peek)?));
    iter.next();
    res
}

/// Ensure the variable should actually exist.
fn get_ident(token: Option<&pm::TokenTree>) -> Result<pm2::Ident, pm2::TokenStream> {
    match token {
        Some(pm::TokenTree::Ident(x))
            if !(["while", "end", "incr", "decr", "clear"]
                .contains(&&*x.to_string().to_lowercase())) =>
        {
            Ok(pm2::Ident::new(&*x.to_string(), pm2::Span::from(x.span())))
        }
        Some(token) => Err(quote_spanned! {
            pm2::Span::from(token.span())=>
            compile_error!("expected a variable.");
        }),
        None => Err(quote! {
            compile_error!("expected a variable.");
        }),
    }
}

/// Check for a semicolon.
fn check_for_semi(token: Option<&pm::TokenTree>, span: pm2::Span) -> Result<(), pm2::TokenStream> {
    match token {
        Some(pm::TokenTree::Punct(x)) if x.as_char() == ';' => Ok(()),
        _ => Err(quote_spanned!(span=> compile_error!("expected a semicolon.");)),
    }
}

/// Check for a zero (used in while command)
fn check_for_zero(token: Option<&pm::TokenTree>, span: pm2::Span) -> Result<(), pm2::TokenStream> {
    match token {
        Some(pm::TokenTree::Literal(x)) if x.to_string() == "0" => Ok(()),
        _ => Err(quote_spanned!(span=> compile_error!("expected a zero.");)),
    }
}

/// Check for some sort of rust identifier, this could be do, not,...
fn check_for_ident(
    token: Option<&pm::TokenTree>,
    span: pm2::Span,
    ident: &str,
) -> Result<(), pm2::TokenStream> {
    let error = format!("expected {}", ident);
    match token {
        Some(pm::TokenTree::Ident(x)) if x.to_string() == ident => Ok(()),
        Some(tok) => Err(quote_spanned!(pm2::Span::from(tok.span())=> compile_error!(#error);)),
        None => Err(quote_spanned!(span=> compile_error!(#error);)),
    }
}
