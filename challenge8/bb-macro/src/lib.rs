#![feature(proc_macro_span)]

use proc_macro::{self, TokenStream, TokenTree};

use quote::{format_ident, quote, quote_spanned};

macro_rules! unwrap_or_return {
    ( $e:expr ) => {
        match $e {
            Ok(x) => x,
            Err(x) => return x,
        }
    };
}

#[proc_macro]
pub fn bb(input: TokenStream) -> TokenStream {
    let temp: TokenStream = bb_to_rust(&mut input.into_iter(), false).into();
    temp
}

fn bb_to_rust(
    iter: &mut proc_macro::token_stream::IntoIter,
    close_while: bool,
) -> proc_macro2::TokenStream {
    let mut expressions = vec![];
    while let Some(token) = iter.next() {
        expressions.push(match token.to_string().to_lowercase().as_str() {
            "incr" => {
                let argument = unwrap_or_return!(create_arg(iter));
                assert_eq!(
                    iter.next().unwrap().to_string(),
                    ";",
                    "Does not correctly close expression."
                );
                quote_spanned!(argument.span()=> #argument += 1;)
            }
            "decr" => {
                let argument = unwrap_or_return!(create_arg(iter));
                assert_eq!(
                    iter.next().unwrap().to_string(),
                    ";",
                    "Does not correctly close expression."
                );
                quote_spanned!(argument.span()=> #argument -= 1;)
            }
            "clear" => {
                let argument = unwrap_or_return!(create_arg(iter));
                assert_eq!(
                    iter.next().unwrap().to_string(),
                    ";",
                    "Does not correctly close expression."
                );
                quote_spanned!(argument.span()=> let mut #argument = 0;)
            }
            "while" => {
                let argument = unwrap_or_return!(create_arg(iter));
                assert_eq!(iter.next().unwrap().to_string().to_lowercase(), "not");
                assert_eq!(iter.next().unwrap().to_string(), "0");
                assert_eq!(iter.next().unwrap().to_string().to_lowercase(), "do");
                assert_eq!(iter.next().unwrap().to_string(), ";");
                let inside_expression = bb_to_rust(iter, true);
                quote_spanned!(
                    argument.span()=>
                    while #argument != 0 {
                        #inside_expression
                    }
                )
            }
            "end" if close_while => {
                assert_eq!(iter.next().unwrap().to_string(), ";");
                return quote!(#(#expressions)*);
            }
            _ => unreachable!(),
        })
    }
    if close_while {
        unreachable!();
    }
    return quote!(#(#expressions)*);
}

fn create_arg(
    iter: &mut proc_macro::token_stream::IntoIter,
) -> Result<proc_macro2::Ident, proc_macro2::TokenStream> {
    let peek = iter.next().unwrap();
    if peek.to_string() == "'" {
        Ok(format_ident!("{}", get_ident(iter.next().unwrap())?))
    } else {
        Ok(format_ident!("__bb_{}", get_ident(peek)?))
    }
}

fn get_ident(token: TokenTree) -> Result<proc_macro2::Ident, proc_macro2::TokenStream> {
    match token {
        TokenTree::Ident(x) => Ok(proc_macro2::Ident::new(
            &*x.to_string(),
            x.span().source().into(),
        )),
        _ => Err(quote_spanned! {
            token.span().source().into() =>
            compile_error!("expected a command.");
        }),
    }
}
