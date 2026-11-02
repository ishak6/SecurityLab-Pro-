
package com.securitylab.pro

import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI

private val Blue = Color(0xFF3978FF)
private val Purple = Color(0xFF9B6BFF)
private val DarkBg = Color(0xFF0B1020)
private val DarkCard = Color(0xFF151D32)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecurityLabApp()
        }
    }
}

private data class AppDestination(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val available: Boolean
)

@Composable
fun SecurityLabApp() {
    var darkMode by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    var latestReport by remember {
        mutableStateOf<SecurityAssessmentResult?>(null)
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val destinations = listOf(
        AppDestination("Dashboard", "Main security dashboard", Icons.Default.Home, true),
        AppDestination("Security Scanner", "HTTPS and security headers assessment", Icons.Default.Security, true),
        AppDestination("Research Browser", "Web browsing and research", Icons.Default.Language, true),
        AppDestination("Recon Studio", "Asset and attack surface discovery", Icons.Default.Search, false),
        AppDestination("Bypass Research", "Authorized access-control analysis", Icons.Default.Policy, false),
        AppDestination("HackBar Studio", "Basic security utilities", Icons.Default.Build, true),
        AppDestination("SQL Injection Lab", "Safe SQL injection learning", Icons.Default.Storage, false),
        AppDestination("Vulnerability Lab", "Web security learning modules", Icons.Default.BugReport, false),
        AppDestination("API Security", "REST and API security testing", Icons.Default.Api, false),
        AppDestination("SSL/TLS Lab", "Certificate and HTTPS security", Icons.Default.Lock, false),
        AppDestination("Bug Bounty Manager", "Programs, scope and findings", Icons.Default.Star, false),
        AppDestination("Report Center", "Latest security assessment report", Icons.Default.Description, true),
        AppDestination("Settings", "Application settings", Icons.Default.Settings, true)
    )

    val bottomTabs = listOf(0, 2, 5, 11, 12)

    val colors = if (darkMode) {
        darkColorScheme(
            primary = Blue,
            secondary = Purple,
            background = DarkBg,
            surface = DarkCard
        )
    } else {
        lightColorScheme(
            primary = Blue,
            secondary = Purple
        )
    }

    BackHandler(enabled = drawerState.isOpen || selectedTab != 0) {
        scope.launch {
            if (drawerState.isOpen) drawerState.close()
            else selectedTab = 0
        }
    }

    MaterialTheme(colorScheme = colors) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = Blue,
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                "SecurityLab Pro",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Ultimate Security Workspace",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "MAIN NAVIGATION",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    destinations.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Column {
                                    Text(item.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        if (item.available) "Available" else "Planned",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            icon = {
                                Icon(item.icon, contentDescription = item.title)
                            },
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(
                                NavigationDrawerItemDefaults.ItemPadding
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider()

                    Text(
                        "Only test systems you own or are authorized to assess.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar {
                        bottomTabs.forEach { index ->
                            val item = destinations[index]

                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = {
                                    Icon(item.icon, contentDescription = item.title)
                                },
                                label = {
                                    Text(
                                        when (index) {
                                            0 -> "Home"
                                            2 -> "Browser"
                                            5 -> "Tools"
                                            11 -> "Reports"
                                            else -> "Settings"
                                        },
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    AppHeader {
                        scope.launch { drawerState.open() }
                    }

                    when (selectedTab) {
                        0 -> HomeScreen(
                            onScan = { selectedTab = 1 },
                            onTools = { selectedTab = 5 },
                            onBrowser = { selectedTab = 2 }
                        )

                        1 -> ScannerScreen(
                            onResult = { latestReport = it }
                        )

                        2 -> ResearchBrowserScreen()

                        5 -> ToolsScreen()

                        11 -> ReportsScreen(latestReport)

                        12 -> SettingsScreen(
                            darkMode = darkMode,
                            onThemeChange = { darkMode = it }
                        )

                        else -> PlannedModuleScreen(destinations[selectedTab])
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu")
        }

        Icon(
            Icons.Default.Shield,
            contentDescription = null,
            tint = Blue,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                "SecurityLab Pro",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Ultimate Security Workspace",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.weight(1f))

        Icon(
            Icons.Default.VerifiedUser,
            contentDescription = "Security status",
            tint = Color(0xFF20C997)
        )
    }
}

@Composable
fun AppCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
    )
}

@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onTools: () -> Unit,
    onBrowser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Security Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Your mobile security workspace",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Blue)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("SECURITY OVERVIEW", color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ready to assess",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Passive HTTPS assessment available.", color = Color.White)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Blue
                    )
                ) {
                    Text("Start Security Assessment")
                }
            }
        }

        SectionTitle("Quick Access")

        AppCard(
            "Security Scanner",
            "HTTPS and security header assessment",
            Icons.Default.Security,
            onScan
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "Research Browser",
            "Browse websites and inspect page loading",
            Icons.Default.Language,
            onBrowser
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "HackBar Studio",
            "JWT decoder, HTTPS checker and header guide",
            Icons.Default.Build,
            onTools
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "Recon Studio",
            "Planned asset discovery workspace",
            Icons.Default.Search
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "SQL Injection Lab",
            "Planned safe SQL learning environment",
            Icons.Default.Storage
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Only assess websites you own or are authorized to assess.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ScannerScreen(onResult: (SecurityAssessmentResult) -> Unit) {
    var target by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("") }
    var authorized by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Ready to assess.") }
    var report by remember { mutableStateOf<SecurityAssessmentResult?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Website Security Scanner",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Checks HTTPS response headers and TLS certificate using a single read-only request.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SectionTitle("Target Information")

        OutlinedTextField(
            value = target,
            onValueChange = { target = it },
            label = { Text("HTTPS Website URL") },
            placeholder = { Text("https://example.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = scope,
            onValueChange = { scope = it },
            label = { Text("Authorized Scope / Notes") },
            placeholder = { Text("Example: My own website") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = authorized,
                onCheckedChange = { authorized = it }
            )
            Text("I own this website or have permission.")
        }

        Button(
            onClick = {
                val valid = runCatching {
                    val uri = URI(target.trim())
                    uri.scheme.equals("https", true) && !uri.host.isNullOrBlank()
                }.getOrDefault(false)

                when {
                    !valid -> message = "Enter a valid HTTPS URL."
                    !authorized -> message = "Authorization confirmation is required."
                    scope.isBlank() -> message = "Describe the authorized scope."
                    else -> {
                        loading = true
                        message = "Connecting securely..."

                        coroutineScope.launch {
                            val response = withContext(Dispatchers.IO) {
                                runCatching {
                                    SecurityAssessmentEngine.inspect(target.trim())
                                }
                            }

                            loading = false

                            response.onSuccess {
                                report = it
                                onResult(it)
                                message = "Assessment completed."
                            }.onFailure {
                                message = "Assessment failed: ${it.message ?: "Connection error"}"
                            }
                        }
                    }
                }
            },
            enabled = !loading && target.isNotBlank() && scope.isNotBlank() && authorized,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Security, contentDescription = null)
            }

            Spacer(Modifier.width(8.dp))
            Text(if (loading) "Assessing..." else "Start Assessment")
        }

        SectionTitle("Assessment Status")

        Card(Modifier.fillMaxWidth()) {
            Text(message, modifier = Modifier.padding(16.dp))
        }

        report?.let { result ->
            SectionTitle("Assessment Results")

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("HTTP Status: ${result.statusCode}", fontWeight = FontWeight.Bold)
                    Text("Website: ${result.url}")
                    Text("Cipher Suite: ${result.cipherSuite}")

                    Spacer(Modifier.height(10.dp))
                    Text("TLS Certificate", fontWeight = FontWeight.Bold)
                    Text("Subject: ${result.certificateSubject}")
                    Text("Expiry: ${result.certificateExpiry}")
                    Text("Checked: ${result.checkedAt}")

                    Spacer(Modifier.height(12.dp))
                    Text("Security Header Findings", fontWeight = FontWeight.Bold)
                    Text("Passed: ${result.passedCount}")
                    Text("Warnings: ${result.warningCount}")

                    result.findings.forEach { finding ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "${if (finding.passed) "✓" else "⚠"} ${finding.name}",
                            fontWeight = FontWeight.Bold,
                            color = if (finding.passed) Color(0xFF20A66A)
                            else MaterialTheme.colorScheme.error
                        )
                        Text(finding.detail)
                    }
                }
            }
        }

        SectionTitle("Assessment Limits")
        Text(
            "This is a passive baseline assessment. It does not perform SQL injection, XSS, authentication bypass, port scanning, or exploitation.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ToolsScreen() {
    var jwt by remember { mutableStateOf("") }
    var jwtResult by remember { mutableStateOf("Decoded token data will appear here.") }
    var url by remember { mutableStateOf("") }
    var urlResult by remember { mutableStateOf("Enter a URL to validate.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Security Tools",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        SectionTitle("JWT Decoder")

        Text("Decodes JWT header and payload locally. Signature is not verified.")

        OutlinedTextField(
            value = jwt,
            onValueChange = { jwt = it },
            label = { Text("Paste JWT Token") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { jwtResult = decodeJwt(jwt) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decode JWT")
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text(jwtResult, modifier = Modifier.padding(14.dp))
        }

        SectionTitle("HTTPS URL Checker")

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Website URL") },
            placeholder = { Text("https://example.com") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { urlResult = validateHttpsUrl(url) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate URL")
        }

        Text(urlResult, modifier = Modifier.padding(top = 12.dp))

        SectionTitle("Security Headers Guide")

        val headers = listOf(
            "Content-Security-Policy" to "Restricts permitted content sources.",
            "Strict-Transport-Security" to "Requests browsers to use HTTPS.",
            "X-Content-Type-Options" to "Helps prevent MIME type sniffing.",
            "Referrer-Policy" to "Controls referrer information.",
            "Permissions-Policy" to "Controls access to browser features."
        )

        headers.forEach { (name, description) ->
            AppCard(name, description, Icons.Default.Security)
            Spacer(Modifier.height(8.dp))
        }
    }
}

fun decodeJwt(token: String): String {
    return try {
        val parts = token.trim().split(".")
        require(parts.size == 3) {
            "Invalid JWT format. Expected three segments."
        }

        fun decodePart(part: String): String {
            val bytes = Base64.decode(
                part,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )
            return bytes.toString(Charsets.UTF_8)
        }

        val header = decodePart(parts[0])
        val payload = decodePart(parts[1])

        "HEADER\n$header\n\nPAYLOAD\n$payload\n\n" +
            "WARNING: Signature has NOT been verified."
    } catch (e: Exception) {
        "Could not decode token: ${e.message ?: "Invalid input"}"
    }
}

fun validateHttpsUrl(input: String): String {
    return try {
        val uri = URI(input.trim())
        when {
            !uri.scheme.equals("https", true) ->
                "Not HTTPS. Use an https:// URL."

            uri.host.isNullOrBlank() ->
                "Invalid URL: hostname is missing."

            else ->
                "Valid HTTPS URL format.\nHost: ${uri.host}\n\n" +
                    "This checks URL format only. Use Scanner for a real connection and certificate assessment."
        }
    } catch (e: Exception) {
        "Invalid URL: ${e.message ?: "Check the address."}"
    }
}

@Composable
fun ReportsScreen(result: SecurityAssessmentResult?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Reports",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        SectionTitle("Latest Assessment")

        if (result == null) {
            AppCard(
                "No reports yet",
                "Run a passive assessment to create a report.",
                Icons.Default.Description
            )
        } else {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(result.url, fontWeight = FontWeight.Bold)
                    Text("HTTP Status: ${result.statusCode}")
                    Text("Passed: ${result.passedCount}")
                    Text("Warnings: ${result.warningCount}")
                    Text("Checked: ${result.checkedAt}")

                    result.findings.forEach {
                        Spacer(Modifier.height(8.dp))
                        Text("${it.name}: ${it.detail}")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "This version displays the latest report during the current app session. Persistent history and PDF export are not included yet.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PlannedModuleScreen(destination: AppDestination) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            destination.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    destination.icon,
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(42.dp)
                )

                Text(
                    "Module Development",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(destination.subtitle)

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (destination.available) "Available"
                            else "Planned Module"
                        )
                    }
                )

                Text(
                    "Navigation is ready. Functional tools for this module will be implemented in a later development phase.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        SectionTitle("Appearance")

        Card(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Dark Theme", fontWeight = FontWeight.Bold)
                    Text(
                        "Switch between dark and light appearance.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Switch(
                    checked = darkMode,
                    onCheckedChange = onThemeChange
                )
            }
        }

        SectionTitle("Application")

        AppCard(
            "Security & Privacy",
            "Permission-first security assessment",
            Icons.Default.PrivacyTip
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "About SecurityLab Pro",
            "Version 1.1 • Android",
            Icons.Default.Info
        )
    }
}
