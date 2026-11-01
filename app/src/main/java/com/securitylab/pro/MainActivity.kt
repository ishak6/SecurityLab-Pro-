package com.securitylab.pro

import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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

private data class TabItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun SecurityLabApp() {
    var darkMode by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem("Home", Icons.Default.Home),
        TabItem("Scanner", Icons.Default.Security),
        TabItem("Tools", Icons.Default.Build),
        TabItem("Reports", Icons.Default.Description),
        TabItem("Settings", Icons.Default.Settings)
    )

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

    MaterialTheme(colorScheme = colors) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(item.icon, contentDescription = item.title)
                            },
                            label = { Text(item.title) }
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
                AppHeader()

                when (selectedTab) {
                    0 -> HomeScreen(
                        onScan = { selectedTab = 1 },
                        onTools = { selectedTab = 2 }
                    )
                    1 -> ScannerScreen()
                    2 -> ToolsScreen()
                    3 -> ReportsScreen()
                    4 -> SettingsScreen(
                        darkMode = darkMode,
                        onThemeChange = { darkMode = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                "Mobile Security Toolkit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Icon(
                icon,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(30.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.weight(1f))
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
    onTools: () -> Unit
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
            colors = CardDefaults.cardColors(
                containerColor = Blue
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "SECURITY OVERVIEW",
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ready to assess",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "No remote scans have been performed.",
                    color = Color.White
                )
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

        SectionTitle("Security Modules")

        AppCard(
            "Security Scanner",
            "Authorized target assessment setup",
            Icons.Default.Search,
            onScan
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "JWT Decoder",
            "Decode token header and payload locally",
            Icons.Default.Key,
            onTools
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "HTTPS Checker",
            "Validate HTTPS URL format",
            Icons.Default.Lock,
            onTools
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "Security Headers Guide",
            "Learn important HTTP security headers",
            Icons.Default.Policy,
            onTools
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Educational tools do not replace a professional security audit.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ScannerScreen() {
    var target by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("") }
    var authorized by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("No assessment started.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "New Security Assessment",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Only assess systems you own or have explicit permission to test.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SectionTitle("Target Information")

        OutlinedTextField(
            value = target,
            onValueChange = { target = it },
            label = { Text("Target URL or Host") },
            placeholder = { Text("https://example.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = scope,
            onValueChange = { scope = it },
            label = { Text("Authorized testing scope") },
            placeholder = { Text("Example: my own staging website") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = authorized,
                onCheckedChange = { authorized = it }
            )
            Text("I own this target or have written permission.")
        }

        Button(
            onClick = {
                message = when {
                    target.isBlank() ->
                        "Enter a target first."
                    scope.isBlank() ->
                        "Describe the authorized scope."
                    !authorized ->
                        "Permission confirmation is required."
                    else ->
                        "Setup validated. Remote scanning engine is not implemented yet."
                }
            },
            enabled = target.isNotBlank() &&
                    scope.isNotBlank() &&
                    authorized,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Validate Assessment Setup")
        }

        SectionTitle("Status")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                message,
                modifier = Modifier.padding(18.dp)
            )
        }

        SectionTitle("Planned Assessment Modules")

        listOf(
            "SQL Injection checks",
            "Cross-Site Scripting checks",
            "API Security checks",
            "Security Header analysis",
            "SSL/TLS certificate inspection"
        ).forEach {
            Text(
                "• $it",
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }

        Text(
            "These remote modules are planned, not active in this build.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
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

        Text(
            "Decodes the JWT header and payload locally. It does not verify the signature."
        )

        OutlinedTextField(
            value = jwt,
            onValueChange = { jwt = it },
            label = { Text("Paste JWT token") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                jwtResult = decodeJwt(jwt)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decode JWT")
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                jwtResult,
                modifier = Modifier.padding(14.dp)
            )
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
            onClick = {
                urlResult = validateHttpsUrl(url)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validate URL")
        }

        Text(
            urlResult,
            modifier = Modifier.padding(top = 12.dp)
        )

        SectionTitle("Security Headers Guide")

        listOf(
            "Content-Security-Policy" to
                    "Helps restrict permitted content sources.",
            "Strict-Transport-Security" to
                    "Requests browsers to use HTTPS.",
            "X-Content-Type-Options" to
                    "Helps prevent MIME type sniffing.",
            "Referrer-Policy" to
                    "Controls referrer information.",
            "Permissions-Policy" to
                    "Controls access to browser features."
        ).forEach { (name, description) ->
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
                Base64.URL_SAFE or
                        Base64.NO_PADDING or
                        Base64.NO_WRAP
            )
            return bytes.toString(Charsets.UTF_8)
        }

        val header = decodePart(parts[0])
        val payload = decodePart(parts[1])

        "HEADER\n$header\n\nPAYLOAD\n$payload\n\n" +
                "Note: Signature has NOT been verified."
    } catch (e: Exception) {
        "Could not decode token: ${e.message ?: "Invalid input"}"
    }
}

fun validateHttpsUrl(input: String): String {
    return try {
        val uri = URI(input.trim())

        when {
            uri.scheme != "https" ->
                "Not HTTPS. Use an https:// URL."
            uri.host.isNullOrBlank() ->
                "Invalid URL: hostname is missing."
            else ->
                "Valid HTTPS URL format.\nHost: ${uri.host}\n\n" +
                        "This checks URL format only. It does not connect " +
                        "to the server or inspect its certificate."
        }
    } catch (e: Exception) {
        "Invalid URL: ${e.message ?: "Check the address."}"
    }
}

@Composable
fun ReportsScreen() {
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

        SectionTitle("Assessment Reports")

        AppCard(
            "No reports yet",
            "Reports will appear here after report generation is implemented.",
            Icons.Default.Description
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "PDF report export is planned and is not available in this build.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
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
            "Local educational tools and permission-first testing",
            Icons.Default.PrivacyTip
        )

        Spacer(Modifier.height(10.dp))

        AppCard(
            "About SecurityLab Pro",
            "Version 1.0 • Android",
            Icons.Default.Info
        )
    }
}
