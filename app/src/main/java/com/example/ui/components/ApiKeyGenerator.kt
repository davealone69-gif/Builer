package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.security.SecureRandom

/**
 * Key types supported by the API Key Generator
 */
enum class ApiKeyType(val displayName: String, val prefix: String, val description: String) {
    GEMINI_API("Gemini / Google AI Studio", "AIzaSy", "Standard Google AI Studio API Key format"),
    BUILDER_LIVE("Builder Production Key", "builder_live_", "Live API key for production app service"),
    BUILDER_SANDBOX("Builder Dev / Sandbox Key", "builder_test_", "Isolated test key for devator lab testing"),
    HUGGING_FACE("Hugging Face Token", "hf_", "Hugging Face User Access Token format"),
    OPEN_ROUTER("OpenRouter API Key", "sk-or-v1-", "OpenRouter multi-model LLM router key format"),
    OLLAMA_KEY("Ollama API Key / Auth Token", "ollama_sk_", "Ollama local / cloud server API key format"),
    X_API_KEY("X-API-KEY Header", "xkey_", "Custom X-API-KEY header token format"),
    HMAC_SECRET("Hmac SHA-256 Secret", "sec_key_", "256-bit cryptographic signing key")
}

/**
 * Utility object for secure key generation and entropy calculation
 */
object ApiKeyGeneratorUtil {

    private val ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".toCharArray()
    private val HEX_CHARS = "0123456789abcdef".toCharArray()
    private val secureRandom = SecureRandom()

    fun generateKey(type: ApiKeyType, entropyLength: Int = 39): String {
        val sb = StringBuilder(type.prefix)
        if (type == ApiKeyType.HMAC_SECRET) {
            val bytes = ByteArray(32)
            secureRandom.nextBytes(bytes)
            for (b in bytes) {
                val v = b.toInt() and 0xFF
                sb.append(HEX_CHARS[v ushr 4])
                sb.append(HEX_CHARS[v and 0x0F])
            }
        } else {
            for (i in 0 until entropyLength) {
                val index = secureRandom.nextInt(ALLOWED_CHARS.size)
                sb.append(ALLOWED_CHARS[index])
            }
        }
        return sb.toString()
    }

    fun calculateEntropyRating(key: String): String {
        return when {
            key.length >= 50 -> "Extreme Entropy (256+ bit)"
            key.length >= 32 -> "High Entropy (192-bit)"
            key.length >= 16 -> "Medium Entropy (128-bit)"
            key.isNotEmpty() -> "Low Entropy"
            else -> "No Key Set"
        }
    }

    fun maskKey(key: String): String {
        if (key.length <= 10) return "••••••••"
        return "${key.take(6)}••••••••${key.takeLast(4)}"
    }
}

/**
 * Interactive API Key Generator Composable Component
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApiKeyGeneratorCard(
    currentApiKey: String,
    onApiKeyGenerated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(ApiKeyType.GEMINI_API) }
    var generatedKey by remember { mutableStateOf(currentApiKey.ifBlank { ApiKeyGeneratorUtil.generateKey(ApiKeyType.GEMINI_API) }) }
    var keyVisible by remember { mutableStateOf(true) }
    var keyEntropyRating by remember { mutableStateOf(ApiKeyGeneratorUtil.calculateEntropyRating(generatedKey)) }
    var lastActionStatus by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("api_key_generator_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Key Generator",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "API Key Generator & Manager",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cryptographic",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Generate high-entropy developer keys or format custom Gemini API tokens securely.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Key Type Selection Chips
            Text(
                text = "Select Key Template / Prefix:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ApiKeyType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            generatedKey = ApiKeyGeneratorUtil.generateKey(type)
                            keyEntropyRating = ApiKeyGeneratorUtil.calculateEntropyRating(generatedKey)
                            lastActionStatus = "Generated ${type.displayName} key"
                        },
                        label = { Text(type.displayName, fontSize = 12.sp) },
                        leadingIcon = if (selectedType == type) {
                            { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.height(16.dp)) }
                        } else null
                    )
                }
            }

            // Key Output & Input Field
            OutlinedTextField(
                value = generatedKey,
                onValueChange = { newValue ->
                    generatedKey = newValue
                    keyEntropyRating = ApiKeyGeneratorUtil.calculateEntropyRating(newValue)
                    lastActionStatus = "Custom key modified"
                },
                label = { Text("${selectedType.displayName} Key") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_key_generator_output"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { keyVisible = !keyVisible }) {
                        Icon(
                            imageVector = if (keyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle key visibility"
                        )
                    }
                }
            )

            // Entropy & Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = keyEntropyRating,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (lastActionStatus != null) {
                    Text(
                        text = lastActionStatus!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generate Fresh Key Button
                Button(
                    onClick = {
                        generatedKey = ApiKeyGeneratorUtil.generateKey(selectedType)
                        keyEntropyRating = ApiKeyGeneratorUtil.calculateEntropyRating(generatedKey)
                        lastActionStatus = "New key generated"
                    },
                    modifier = Modifier.weight(1f).testTag("btn_generate_fresh_key"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Generate", fontSize = 12.sp)
                }

                // Copy to Clipboard Button
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("API Key", generatedKey)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "API Key copied to clipboard", Toast.LENGTH_SHORT).show()
                        lastActionStatus = "Copied to clipboard"
                    },
                    modifier = Modifier.weight(1f).testTag("btn_copy_generated_key"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp)
                }

                // Apply to Active App Setting
                Button(
                    onClick = {
                        onApiKeyGenerated(generatedKey)
                        Toast.makeText(context, "API Key updated in app state!", Toast.LENGTH_SHORT).show()
                        lastActionStatus = "Applied to Active Key"
                    },
                    modifier = Modifier.weight(1f).testTag("btn_apply_active_key"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Apply", fontSize = 12.sp)
                }
            }

            // Info note for official Google AI Studio key
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Need an Official Google AI Studio Key?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Obtain real Gemini 1.5/2.5 API credentials from Google AI Studio portal.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "aistudio.google.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
