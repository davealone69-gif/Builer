package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AndroidSdkPackageInfo(
    val name: String,
    val namespaceGroup: String, // "android.*", "androidx.*", "kotlinx.* / Java"
    val description: String,
    val keyClasses: String
)

val ANDROID_PACKAGES_DATA = listOf(
    // android.*
    AndroidSdkPackageInfo("android.app", "android.*", "Core application model, Activity, Service, NotificationManager, Application, PendingIntent, and Dialog classes.", "Activity, Service, NotificationManager, Application"),
    AndroidSdkPackageInfo("android.content", "android.*", "Data access, Intent messaging, BroadcastReceiver, ContentProvider, Context, and SharedPreferences.", "Context, Intent, ContentProvider, BroadcastReceiver"),
    AndroidSdkPackageInfo("android.os", "android.*", "Operating system services, Handler, Looper, Message, Bundle, Parcelable, Process, and PowerManager.", "Bundle, Handler, Message, Parcelable, Build"),
    AndroidSdkPackageInfo("android.view", "android.*", "Core UI framework, View, ViewGroup, MotionEvent, Window, Surface, and LayoutInflater.", "View, ViewGroup, MotionEvent, WindowManager"),
    AndroidSdkPackageInfo("android.widget", "android.*", "Pre-built UI views such as TextView, Button, ImageView, Toast, ProgressBar, and ListView.", "TextView, ImageView, Button, Toast, ProgressBar"),
    AndroidSdkPackageInfo("android.graphics", "android.*", "Low-level 2D graphics, Canvas, Paint, Bitmap, Color, Matrix, and Path rendering.", "Canvas, Paint, Bitmap, Color, Path"),
    AndroidSdkPackageInfo("android.hardware", "android.*", "Hardware abstraction interfaces for Camera, Sensors, Biometrics, and USB devices.", "Sensor, SensorManager, CameraManager"),
    AndroidSdkPackageInfo("android.location", "android.*", "Location providers, LocationManager, Location, and Geocoder services.", "LocationManager, Location, Geocoder"),
    AndroidSdkPackageInfo("android.net", "android.*", "Network connectivity, ConnectivityManager, Uri, NetworkRequest, and HTTP sockets.", "ConnectivityManager, Uri, NetworkRequest"),
    AndroidSdkPackageInfo("android.security", "android.*", "Security providers, KeyStore, KeyGenParameterSpec, and MasterKey cryptographic state.", "KeyStore, KeyPairGenerator, KeyGenParameterSpec"),
    AndroidSdkPackageInfo("android.telephony", "android.*", "Cellular networks, TelephonyManager, SmsManager, and SIM card details.", "TelephonyManager, SmsManager, SignalStrength"),
    AndroidSdkPackageInfo("android.bluetooth", "android.*", "Bluetooth LE and Classic APIs: BluetoothAdapter, BluetoothDevice, and GATT sockets.", "BluetoothAdapter, BluetoothDevice, BluetoothGatt"),
    AndroidSdkPackageInfo("android.media", "android.*", "Audio/video playback & encoding: MediaPlayer, AudioRecord, MediaCodec, and AudioManager.", "MediaPlayer, AudioManager, AudioRecord, MediaCodec"),
    AndroidSdkPackageInfo("android.accessibilityservice", "android.*", "Accessibility services, AccessibilityEvent, and user interaction feedback.", "AccessibilityService, AccessibilityNodeInfo"),
    
    // androidx.*
    AndroidSdkPackageInfo("androidx.compose.ui", "androidx.*", "Jetpack Compose UI framework core, Modifier, Alignment, Canvas, Layout, and DrawScope.", "Modifier, Alignment, Canvas, Composables"),
    AndroidSdkPackageInfo("androidx.compose.material3", "androidx.*", "Material Design 3 components: Scaffold, Button, Card, Text, TextField, NavigationBar, TopAppBar.", "Scaffold, Card, Button, Text, OutlinedTextField"),
    AndroidSdkPackageInfo("androidx.compose.runtime", "androidx.*", "Compose compiler runtime, state management: remember, mutableStateOf, LaunchedEffect, produceState.", "remember, mutableStateOf, LaunchedEffect, State"),
    AndroidSdkPackageInfo("androidx.core.ktx", "androidx.*", "Kotlin extensions and backward-compatible wrappers for core Android platform APIs.", "Context.getSystemService(), bundleOf(), String.toUri()"),
    AndroidSdkPackageInfo("androidx.activity", "androidx.*", "ComponentActivity, setContent, back press handlers, and Activity Result API contracts.", "ComponentActivity, setContent, OnBackPressedCallback"),
    AndroidSdkPackageInfo("androidx.lifecycle", "androidx.*", "Lifecycle-aware components: ViewModel, StateFlow, LifecycleOwner, collectAsStateWithLifecycle.", "ViewModel, MutableStateFlow, LifecycleOwner"),
    AndroidSdkPackageInfo("androidx.navigation", "androidx.*", "Jetpack Navigation Compose for type-safe screen routing and deep linking.", "NavHost, composable(), rememberNavController()"),
    AndroidSdkPackageInfo("androidx.room", "androidx.*", "Room local persistence ORM over SQLite with KSP support and coroutine Flow queries.", "RoomDatabase, @Entity, @Dao, @Query"),
    AndroidSdkPackageInfo("androidx.work", "androidx.*", "WorkManager for guaranteed deferrable, background task execution and job constraints.", "WorkManager, CoroutineWorker, OneTimeWorkRequestBuilder"),
    AndroidSdkPackageInfo("androidx.datastore", "androidx.*", "DataStore Preferences and Proto DataStore for key-value pair local storage.", "DataStore, PreferenceDataStoreFactory, edit()"),
    AndroidSdkPackageInfo("androidx.camera", "androidx.*", "CameraX lifecycle-aware camera capture, preview, and image analysis library.", "ProcessCameraProvider, Preview, ImageCapture"),
    AndroidSdkPackageInfo("androidx.biometric", "androidx.*", "BiometricPrompt for hardware fingerprint and face recognition authentication.", "BiometricPrompt, BiometricManager, CryptoObject"),

    // kotlinx / java
    AndroidSdkPackageInfo("kotlinx.coroutines", "kotlinx.* / Java", "Asynchronous non-blocking concurrency: Dispatchers, Flow, launch, async, withContext.", "Dispatchers, Flow, StateFlow, CoroutineScope"),
    AndroidSdkPackageInfo("kotlinx.serialization", "kotlinx.* / Java", "Type-safe JSON serialization compiler plugin and runtime encoder/decoder.", "Json, @Serializable, Json.decodeFromString"),
    AndroidSdkPackageInfo("java.net", "kotlinx.* / Java", "Network socket, URLConnection, HttpURLConnection, and InetAddress networking utilities.", "URL, HttpURLConnection, Socket, InetAddress"),
    AndroidSdkPackageInfo("javax.crypto", "kotlinx.* / Java", "Cryptographic ciphers, AES/RSA encryption, Cipher, SecretKey, and Mac algorithms.", "Cipher, SecretKeySpec, Mac, KeyGenerator"),
    AndroidSdkPackageInfo("org.json", "kotlinx.* / Java", "Standard lightweight JSON parsing library: JSONObject and JSONArray.", "JSONObject, JSONArray, JSONException")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AndroidPackageIndexCard(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("All") }

    val filteredPackages = ANDROID_PACKAGES_DATA.filter { pkg ->
        val matchesGroup = when (selectedGroup) {
            "android.*" -> pkg.namespaceGroup == "android.*"
            "androidx.*" -> pkg.namespaceGroup == "androidx.*"
            "kotlinx / Java" -> pkg.namespaceGroup == "kotlinx.* / Java"
            else -> true
        }
        val matchesSearch = pkg.name.contains(searchQuery, ignoreCase = true) ||
                pkg.description.contains(searchQuery, ignoreCase = true) ||
                pkg.keyClasses.contains(searchQuery, ignoreCase = true)

        matchesGroup && matchesSearch
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("android_package_index_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Android Package Index",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Android API Package Index",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "developer.android.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Interactive SDK Package Reference for https://developer.android.com/reference/packages",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Filter Packages (e.g. app, compose, room, os)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("package_index_search_input")
            )

            // Category Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "android.*", "androidx.*", "kotlinx / Java").forEach { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { selectedGroup = group },
                        label = { Text(group, fontSize = 11.sp) }
                    )
                }
            }

            Text(
                text = "Package Reference Results (${filteredPackages.size}):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredPackages.take(8).forEach { pkg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = pkg.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = pkg.namespaceGroup,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = pkg.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Key Symbols: ${pkg.keyClasses}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
