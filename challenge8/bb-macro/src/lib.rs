#![feature(proc_macro_span)]
#![feature(proc_macro_span_shrink)]

use proc_macro as pm;

use proc_macro2 as pm2;
use quote::{format_ident, quote, quote_spanned};
use syn::spanned::Spanned;

macro_rules! simple_expression {
    ( $argument:ident, $expressions:ident, $errors:ident, $iter:ident, $quote:expr ) => {
        match create_arg($iter) {
            Ok(ok) => {
                $argument = ok;
                match check_for_semi($iter.next(), $argument.span()) {
                    Ok(_) => $expressions.push($quote),
                    Err(e) => $errors.push(e),
                }
            }
            Err(e) => $errors.push(e),
        };
    };
}

#[proc_macro]
pub fn bb(input: pm::TokenStream) -> pm::TokenStream {
    let test = bb_to_rust(&mut input.into_iter(), false);
    dbg!(test.clone());
    test.into()
}

fn bb_to_rust(
    iter: &mut proc_macro::token_stream::IntoIter,
    close_while: bool,
) -> pm2::TokenStream {
    let mut expressions = vec![];
    let mut errors = vec![];
    while let Some(token) = iter.next() {
        dbg!(token.clone());
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
                    check_for_ident(iter.next(), argument.span(), "not")?;
                    check_for_zero(iter.next(), argument.span())?;
                    check_for_ident(iter.next(), argument.span(), "do")?;
                    check_for_semi(iter.next(), argument.span())?;
                    let inside_expression = bb_to_rust(iter, true);
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
            "end" if close_while => {
                dbg!("end");
                dbg!(expressions.clone());
                if let Err(e) = check_for_semi(iter.next(), pm2::Span::from(token.span())) {
                    errors.push(e);
                }
                return if errors.is_empty() {
                    quote!(#(#expressions)*)
                } else {
                    quote!(#(#errors)*)
                };
            }
            _ => {
                dbg!(token.clone());
                let message = format!("Unexpected expression: \"{}\".", token);
                errors.push(quote_spanned! {
                    pm2::Span::from(token.span())=>
                    compile_error!(#message);
                });
            }
        }
    }
    dbg!(expressions.clone());
    if errors.is_empty() {
        quote!(#(#expressions)*)
    } else {
        if close_while {
            errors.push(quote_spanned! {
                expressions.first().span()=>
                compile_error!("Unclosed while loop.");
            });
        }
        quote!(#(#errors)*)
    }
}

fn create_arg(
    iter: &mut proc_macro::token_stream::IntoIter,
) -> Result<pm2::Ident, pm2::TokenStream> {
    let peek = iter.next();
    dbg!(peek.clone());
    if let Some(ref peek) = peek {
        if peek.to_string() == "'" {
            return Ok(format_ident!("{}", get_ident(iter.next())?));
        }
    }
    Ok(format_ident!("__bb_{}", get_ident(peek)?))
}

fn get_ident(token: Option<pm::TokenTree>) -> Result<pm2::Ident, pm2::TokenStream> {
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

fn check_for_semi(token: Option<pm::TokenTree>, span: pm2::Span) -> Result<(), pm2::TokenStream> {
    dbg!(token.clone());
    match token {
        Some(pm::TokenTree::Punct(x)) if x.as_char() == ';' => {
            dbg!("yes");
            Ok(())
        }
        _ => Err(quote_spanned!(span=> compile_error!("expected a semi colon.");)),
    }
}

fn check_for_zero(token: Option<pm::TokenTree>, span: pm2::Span) -> Result<(), pm2::TokenStream> {
    match token {
        Some(pm::TokenTree::Literal(x)) if x.to_string() == "0" => Ok(()),
        _ => Err(quote_spanned!(span=> compile_error!("expected a zero.");)),
    }
}

fn check_for_ident(
    token: Option<pm::TokenTree>,
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
