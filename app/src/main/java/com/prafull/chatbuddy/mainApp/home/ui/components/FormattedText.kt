package com.prafull.chatbuddy.mainApp.home.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormattedText(text: String, modifier: Modifier = Modifier) {
    val formattedParts = remember(text) { parseMarkdown(text) }
    Column(modifier = modifier) {
        formattedParts.forEach { part ->
            when (part) {
                is AnnotatedText -> ClickableText(
                        text = part.text,
                        modifier = Modifier.padding()
                )

                is CodeBlock -> CodeSnippetBox(language = part.language, code = part.code)
            }
        }
    }
}

sealed class ParsedPart
data class AnnotatedText(val text: AnnotatedString) : ParsedPart()
data class CodeBlock(val language: String, val code: String) : ParsedPart()

fun parseMarkdown(input: String): List<ParsedPart> {
    val parts = mutableListOf<ParsedPart>()
    val codeBlockRegex = Regex("```(\\w+)?\\s*([\\s\\S]+?)\\s*```")
    val linkRegex = Regex("\\[(.+?)]\\((.+?)\\)")
    var lastIndex = 0

    codeBlockRegex.findAll(input).forEach { matchResult ->
        val range = matchResult.range
        if (range.first > lastIndex) {
            val textPart = input.substring(lastIndex, range.first).trim()
            if (textPart.isNotEmpty()) {
                parts.add(AnnotatedText(parseInlineMarkdown(textPart, linkRegex)))
            }
        }
        val language = matchResult.groups[1]?.value ?: "text"
        val code = matchResult.groups[2]?.value?.trim() ?: ""
        parts.add(CodeBlock(language = language, code = code))
        lastIndex = range.last + 1
    }
    if (lastIndex < input.length) {
        val remainingText = input.substring(lastIndex).trim()
        if (remainingText.isNotEmpty()) {
            parts.add(AnnotatedText(parseInlineMarkdown(remainingText, linkRegex)))
        }
    }
    return parts
}

fun parseInlineMarkdown(input: String, linkRegex: Regex): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < input.length) {
            when {
                input.startsWith("**", i) -> {
                    val end = input.indexOf("**", i + 2)
                    if (end != -1) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(input.substring(i + 2, end))
                        }
                        i = end + 2
                    } else {
                        append("**")
                        i += 2
                    }
                }

                input.startsWith("*", i) -> {
                    val end = input.indexOf("*", i + 1)
                    if (end != -1) {
                        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(input.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append("*")
                        i += 1
                    }
                }

                input.startsWith("`", i) -> {
                    val end = input.indexOf("`", i + 1)
                    if (end != -1) {
                        withStyle(
                                style = SpanStyle(
                                        fontFamily = FontFamily.Default,
                                        background = Color.LightGray
                                )
                        ) {
                            append(input.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append("`")
                        i += 1
                    }
                }

                linkRegex.find(input, i)?.range?.first == i -> {
                    val matchResult = linkRegex.find(input, i)!!
                    val linkText = matchResult.groups[1]!!.value
                    val linkUrl = matchResult.groups[2]!!.value
                    pushStringAnnotation(
                            tag = "URL",
                            annotation = linkUrl
                    )
                    withStyle(
                            style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                            )
                    ) {
                        append(linkText)
                    }
                    pop()
                    i = matchResult.range.last + 1
                }

                else -> {
                    append(input[i])
                    i++
                }
            }
        }
    }
}

@Composable
fun CodeSnippetBox(language: String, code: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                .fillMaxWidth()
    ) {
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = language,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
            )
            TextButton(onClick = {
                clipboardManager.setText(AnnotatedString(code))
                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Copy code", color = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }
        Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(16.dp)
                    .fillMaxWidth()
        ) {
            Text(
                    text = code,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ClickableText(modifier: Modifier = Modifier, text: AnnotatedString) {
    val context = LocalContext.current

    Text(
            text = text,
            modifier = modifier.clickable {
                text.getStringAnnotations("URL", 0, text.length).firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
            },
            style = TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun LazyFormattedTextList(messages: List<String>) {
    LazyColumn {
        items(messages) { message ->
            FormattedText(message)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormattedText() {
    MaterialTheme {
        Surface {
            LazyFormattedTextList(
                    messages = listOf(
                            "This is **bold** text, this is *italic* text, and this is `inline code`. Here is a code block:\n```javascript\nlet a = 5;\nlet b = 10;\n\n[a, b] = [b, a];\n\nconsole.log(a); // 10\nconsole.log(b); // 5\n``` Here is a [link](https://example.com).",
                            "Another message with **bold** text and a code block:\n```python\ndef hello_world():\n    print('Hello, world!')\n```",
                            "Yet another message with *italic* text and an `inline code` example."
                    )
            )
        }
    }
}
