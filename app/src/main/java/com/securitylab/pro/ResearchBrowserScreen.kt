
package com.securitylab.pro

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ResearchBrowserScreen() {
    var address by remember {
        mutableStateOf("https://www.google.com")
    }

    var currentUrl by remember {
        mutableStateOf("https://www.google.com")
    }

    var pageTitle by remember {
        mutableStateOf("Research Browser")
    }

    var progress by remember {
        mutableIntStateOf(0)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var desktopMode by remember {
        mutableStateOf(false)
    }

    var pageError by remember {
        mutableStateOf("")
    }

    var webView by remember {
        mutableStateOf<WebView?>(null)
    }

    fun navigate(input: String) {
        val value = input.trim()
        if (value.isBlank()) return

        val normalized = when {
            value.startsWith("https://", true) ||
                value.startsWith("http://", true) -> value

            value.contains(" ") -> {
                "https://www.google.com/search?q=" +
                    java.net.URLEncoder.encode(value, "UTF-8")
            }

            else -> "https://$value"
        }

        val valid = runCatching {
            val uri = java.net.URI(normalized)
            (uri.scheme == "https" || uri.scheme == "http") &&
                !uri.host.isNullOrBlank()
        }.getOrDefault(false)

        if (!valid) {
            pageError = "Invalid website address."
            return
        }

        pageError = ""
        address = normalized
        currentUrl = normalized
        webView?.loadUrl(normalized)
    }

    BackHandler(
        enabled = webView?.canGoBack() == true
    ) {
        webView?.goBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.destroy()
            webView = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Security Research Browser",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )

        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            singleLine = true,
            label = {
                Text("Website URL")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Language,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        navigate(address)
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Go"
                    )
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = webView?.canGoBack() == true,
                onClick = {
                    webView?.goBack()
                }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            IconButton(
                enabled = webView?.canGoForward() == true,
                onClick = {
                    webView?.goForward()
                }
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Forward"
                )
            }

            IconButton(
                onClick = {
                    webView?.reload()
                }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reload"
                )
            }

            IconButton(
                onClick = {
                    navigate("https://www.google.com")
                }
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Desktop",
                    style = MaterialTheme.typography.labelSmall
                )

                Switch(
                    checked = desktopMode,
                    onCheckedChange = { enabled ->
                        desktopMode = enabled
                        webView?.let { browser ->
                            val settings = browser.settings

                            settings.userAgentString =
                                if (enabled) {
                                    "Mozilla/5.0 (X11; Linux x86_64) " +
                                        "AppleWebKit/537.36 " +
                                        "(KHTML, like Gecko) " +
                                        "Chrome/120.0.0.0 Safari/537.36"
                                } else {
                                    WebView(context = browser.context)
                                        .settings.userAgentString
                                }

                            browser.reload()
                        }
                    }
                )
            }
        }

        if (loading) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (pageError.isNotBlank()) {
            Text(
                pageError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(12.dp)
            )
        }

        Text(
            pageTitle,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        )

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.javaScriptCanOpenWindowsAutomatically = false
                    settings.setSupportMultipleWindows(false)
                    settings.mixedContentMode =
                        android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW

                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            loading = true
                            pageError = ""

                            if (url != null) {
                                currentUrl = url
                                address = url
                            }
                        }

                        override fun onPageFinished(
                            view: WebView?,
                            url: String?
                        ) {
                            loading = false
                            progress = 100

                            if (url != null) {
                                currentUrl = url
                                address = url
                            }

                            pageTitle =
                                view?.title ?: "Research Browser"
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val uri = request?.url ?: return true

                            return when (uri.scheme?.lowercase()) {
                                "https", "http" -> false
                                else -> true
                            }
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(
                            view: WebView?,
                            newProgress: Int
                        ) {
                            progress = newProgress
                            loading = newProgress < 100
                        }

                        override fun onReceivedTitle(
                            view: WebView?,
                            title: String?
                        ) {
                            pageTitle = title ?: "Research Browser"
                        }
                    }

                    webView = this
                    loadUrl(currentUrl)
                }
            },
            update = { view ->
                webView = view
            }
        )

        Text(
            "Use only authorized websites for security research. " +
                "This browser does not perform exploitation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
        )
    }
}
