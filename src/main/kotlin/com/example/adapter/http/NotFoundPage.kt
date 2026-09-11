package com.example.adapter.http

import io.ktor.http.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML

// Keeping the document in a typed DSL makes the error page safer to evolve than an HTML string.
fun notFoundPage(status: HttpStatusCode): String = createHTML().html {
    lang = "en"
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1")
        title("${status.value} | Page not found")
        style {
            unsafe {
                raw(
                    """
                    :root { color-scheme: dark; font-family: Georgia, serif; }
                    * { box-sizing: border-box; }
                    body {
                        margin: 0;
                        min-height: 100vh;
                        display: grid;
                        place-items: center;
                        color: #f4f1ea;
                        background: #14211f;
                    }
                    main { width: min(38rem, 90vw); padding: 3rem 0; }
                    .code { margin: 0; color: #f0a35b; font: 700 clamp(6rem, 25vw, 12rem)/.8 Georgia, serif; letter-spacing: -0.08em; }
                    h1 { margin: 2rem 0 .75rem; font-size: clamp(2rem, 7vw, 4rem); font-weight: 400; }
                    p { max-width: 30rem; margin: 0; color: #b7c4bc; font: 1.1rem/1.6 system-ui, sans-serif; }
                    a { display: inline-block; margin-top: 2rem; color: #14211f; background: #f0a35b; padding: .8rem 1.1rem; text-decoration: none; font: 700 .95rem system-ui, sans-serif; }
                    a:hover { background: #ffd09d; }
                    """.trimIndent(),
                )
            }
        }
    }
    body {
        main {
            p(classes = "code") { +status.value.toString() }
            h1 { +"That page wandered off." }
            p { +"The address you followed does not point to anything here." }
            a(href = "/") { +"Return home" }
        }
    }
}
