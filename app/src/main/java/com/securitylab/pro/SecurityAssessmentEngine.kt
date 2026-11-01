package com.securitylab.pro

import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import java.security.cert.X509Certificate

data class HeaderFinding(
    val name: String,
    val detail: String,
    val passed: Boolean
)

data class SecurityAssessmentResult(
    val url: String,
    val statusCode: Int,
    val cipherSuite: String,
    val certificateSubject: String,
    val certificateExpiry: String,
    val checkedAt: String,
    val findings: List<HeaderFinding>
) {
    val passedCount: Int
        get() = findings.count { it.passed }

    val warningCount: Int
        get() = findings.count { !it.passed }
}

object SecurityAssessmentEngine {

    fun inspect(address: String): SecurityAssessmentResult {
        val url = URL(address)

        require(url.protocol.equals("https", true)) {
            "Only HTTPS URLs are supported."
        }

        require(url.host.isNotBlank()) {
            "Hostname is missing."
        }

        val connection =
            url.openConnection() as HttpsURLConnection

        connection.apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            instanceFollowRedirects = false
            useCaches = false

            setRequestProperty(
                "User-Agent",
                "SecurityLab-Pro/1.1"
            )

            setRequestProperty("Range", "bytes=0-0")
        }

        try {
            val status = connection.responseCode

            val headers = connection.headerFields.entries
                .filter { it.key != null }
                .associate {
                    it.key!!.lowercase(Locale.ROOT) to
                        (it.value?.joinToString(", ") ?: "")
                }

            val certificate =
                connection.serverCertificates
                    .firstOrNull() as? X509Certificate

            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            )

            val findings = mutableListOf<HeaderFinding>()

            fun checkHeader(
                key: String,
                title: String,
                recommendation: String
            ) {
                val value = headers[key]

                findings.add(
                    if (value.isNullOrBlank()) {
                        HeaderFinding(
                            title,
                            "Missing. $recommendation",
                            false
                        )
                    } else {
                        HeaderFinding(
                            title,
                            "Present: $value",
                            true
                        )
                    }
                )
            }

            checkHeader(
                "strict-transport-security",
                "HSTS",
                "Consider Strict-Transport-Security."
            )

            checkHeader(
                "content-security-policy",
                "Content Security Policy",
                "Consider an appropriate CSP policy."
            )

            checkHeader(
                "x-content-type-options",
                "X-Content-Type-Options",
                "Consider setting nosniff."
            )

            checkHeader(
                "referrer-policy",
                "Referrer Policy",
                "Set an appropriate referrer policy."
            )

            checkHeader(
                "permissions-policy",
                "Permissions Policy",
                "Restrict unnecessary browser features."
            )

            val expiry = certificate?.notAfter?.let {
                dateFormat.format(it)
            } ?: "Unavailable"

            if (certificate != null) {
                val expired =
                    certificate.notAfter.before(Date())

                findings.add(
                    HeaderFinding(
                        "TLS Certificate",
                        if (expired) {
                            "Certificate has expired."
                        } else {
                            "Certificate is currently within its validity period."
                        },
                        !expired
                    )
                )
            } else {
                findings.add(
                    HeaderFinding(
                        "TLS Certificate",
                        "Certificate information unavailable.",
                        false
                    )
                )
            }

            return SecurityAssessmentResult(
                url = address,
                statusCode = status,
                cipherSuite =
                    connection.cipherSuite ?: "Unavailable",
                certificateSubject =
                    certificate?.subjectX500Principal?.name
                        ?: "Unavailable",
                certificateExpiry = expiry,
                checkedAt = dateFormat.format(Date()),
                findings = findings
            )

        } finally {
            connection.disconnect()
        }
    }
}
