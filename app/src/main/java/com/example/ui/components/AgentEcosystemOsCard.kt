package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class representing an OS Command Board Item
data class OsBoardItem(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // Agent, Project, Build, Bug, Release, Research, Infra
    val priority: String, // P0, P1, P2, P3
    val ownerAgent: String, // Coder, Researcher, Deployer, Reviewer, MatrixCore
    val relatedRepo: String,
    val targetDate: String,
    val status: String, // Inbox, Triaged, Agent Assigned, In Progress, Blocked, Review/QA, Done/Released, Archived
    val processId: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentEcosystemOsCard() {
    val context = LocalContext.current

    // Board Status Columns
    val statusColumns = listOf(
        "Inbox / New",
        "Triaged",
        "Agent Assigned",
        "In Progress",
        "Blocked",
        "Review / QA",
        "Done / Released",
        "Archived"
    )

    var selectedStatusTab by remember { mutableIntStateOf(3) } // Default: In Progress
    var selectedTypeFilter by remember { mutableStateOf("All") } // All, Agent, Project, Build, Bug, Release
    var showCreateDialog by remember { mutableStateOf(false) }

    // Board Items List (In-memory reactive state)
    val boardItems = remember {
        mutableStateListOf(
            OsBoardItem(
                id = "TASK-101",
                title = "MatrixCore Law Engine Enforcement",
                description = "Enforce zero-trust policy and pre-flight clamping across all agent builds",
                type = "Project",
                priority = "P0",
                ownerAgent = "MatrixCore Agent",
                relatedRepo = "drivelog/builder-core",
                targetDate = "2026-08-15",
                status = "In Progress",
                processId = "PID-8802"
            ),
            OsBoardItem(
                id = "TASK-102",
                title = "HorizontalDivider API Fix in Compose Templates",
                description = "Update Compose BOM to 2024.02.00+ to fix HorizontalDivider compilation error in self-healing pipeline",
                type = "Bug",
                priority = "P0",
                ownerAgent = "Coder Agent",
                relatedRepo = "drivelog/app-factory",
                targetDate = "2026-08-08",
                status = "Done / Released",
                processId = "PID-4412"
            ),
            OsBoardItem(
                id = "TASK-103",
                title = "Mandela vs Matrix Re-imagenator Dual Glitch Engine",
                description = "Full Flutter & Compose dual reality generator with side-by-side glitch comparison",
                type = "Release",
                priority = "P1",
                ownerAgent = "Deployer Agent",
                relatedRepo = "drivelog/mandela-matrix",
                targetDate = "2026-08-07",
                status = "Done / Released",
                processId = "PID-9901"
            ),
            OsBoardItem(
                id = "TASK-104",
                title = "Lyria & Veo Media Generation Orchestrator",
                description = "Integrate Google Lyria audio model and Veo video synthesis into Media Studio card",
                type = "Agent",
                priority = "P1",
                ownerAgent = "Researcher Agent",
                relatedRepo = "drivelog/gemini-orchestrator",
                targetDate = "2026-08-10",
                status = "In Progress",
                processId = "PID-3321"
            ),
            OsBoardItem(
                id = "TASK-105",
                title = "Zero-Trust GitHub PAT Rotation",
                description = "Purge hardcoded plain text tokens from local telemetry DBs and env files",
                type = "Infra",
                priority = "P0",
                ownerAgent = "Reviewer Agent",
                relatedRepo = "drivelog/security-vault",
                targetDate = "2026-08-08",
                status = "Agent Assigned",
                processId = "PID-1109"
            ),
            OsBoardItem(
                id = "TASK-106",
                title = "Self-Healing App Factory 20-Cycle Loop",
                description = "Trigger GitHub Actions CI build, poll runs, download failure logs and feed into Gemini 3.5 Flash patch plan",
                type = "Build",
                priority = "P1",
                ownerAgent = "Coder Agent",
                relatedRepo = "drivelog/ci-runner",
                targetDate = "2026-08-12",
                status = "In Progress",
                processId = "PID-7711"
            )
        )
    }

    // New Item Inputs
    var newTitle by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("Project") }
    var newPriority by remember { mutableStateOf("P1") }
    var newOwner by remember { mutableStateOf("Coder Agent") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agent_ecosystem_os_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperBoard,
                            contentDescription = "Agent OS Board",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Agent Ecosystem OS Command Centre",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agents = Processes | Projects = Services | Builds = Jobs | Bugs = Interrupts",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = { showCreateDialog = !showCreateDialog },
                    modifier = Modifier.testTag("btn_toggle_add_task")
                ) {
                    Icon(
                        imageVector = if (showCreateDialog) Icons.Default.Refresh else Icons.Default.Add,
                        contentDescription = "New Process Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Operating System Metrics Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OsMetricItem(
                        icon = Icons.Default.Android,
                        label = "Processes",
                        value = "${boardItems.count { it.type == "Agent" || it.status == "In Progress" }} Active"
                    )
                    OsMetricItem(
                        icon = Icons.Default.Storage,
                        label = "Services",
                        value = "${boardItems.count { it.type == "Project" }} Running"
                    )
                    OsMetricItem(
                        icon = Icons.Default.Terminal,
                        label = "Jobs",
                        value = "${boardItems.count { it.type == "Build" }} Queued"
                    )
                    OsMetricItem(
                        icon = Icons.Default.BugReport,
                        label = "Interrupts",
                        value = "${boardItems.count { it.type == "Bug" }} Fixed"
                    )
                }
            }

            // Create New Process / Task Form
            AnimatedVisibility(visible = showCreateDialog) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Dispatch New Process / Interrupt / Job", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Task Title / Specification") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newType,
                                onValueChange = { newType = it },
                                label = { Text("Type (Agent/Project/Bug)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = newPriority,
                                onValueChange = { newPriority = it },
                                label = { Text("Priority (P0/P1/P2)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    boardItems.add(
                                        0,
                                        OsBoardItem(
                                            id = "TASK-${System.currentTimeMillis() % 10000}",
                                            title = newTitle,
                                            description = "Spawned process via OS Command Centre UI",
                                            type = newType,
                                            priority = newPriority,
                                            ownerAgent = newOwner,
                                            relatedRepo = "drivelog/builder-workspace",
                                            targetDate = "2026-08-15",
                                            status = "Inbox / New",
                                            processId = "PID-${(1000..9999).random()}"
                                        )
                                    )
                                    newTitle = ""
                                    showCreateDialog = false
                                    Toast.makeText(context, "New Agent Task Dispatched to Command Board!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Dispatch to OS Command Queue")
                        }
                    }
                }
            }

            // Status Columns Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedStatusTab,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 4.dp
            ) {
                statusColumns.forEachIndexed { index, colName ->
                    val count = boardItems.count { it.status == colName }
                    Tab(
                        selected = selectedStatusTab == index,
                        onClick = { selectedStatusTab = index },
                        text = {
                            Text(
                                text = "$colName ($count)",
                                fontSize = 11.sp,
                                fontWeight = if (selectedStatusTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Filter Chips (Type Filters)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("All", "Agent", "Project", "Build", "Bug", "Release", "Infra").forEach { typeName ->
                    FilterChip(
                        selected = selectedTypeFilter == typeName,
                        onClick = { selectedTypeFilter = typeName },
                        label = { Text(typeName, fontSize = 10.sp) }
                    )
                }
            }

            // Current Column Task Items
            val currentStatusName = statusColumns[selectedStatusTab]
            val filteredItems = boardItems.filter { item ->
                item.status == currentStatusName && (selectedTypeFilter == "All" || item.type == selectedTypeFilter)
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active processes in $currentStatusName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredItems.forEach { item ->
                        OsBoardItemCard(
                            item = item,
                            onPromoteStatus = {
                                val nextIndex = (selectedStatusTab + 1) % statusColumns.size
                                val nextStatus = statusColumns[nextIndex]
                                val itemIndex = boardItems.indexOfFirst { it.id == item.id }
                                if (itemIndex != -1) {
                                    boardItems[itemIndex] = item.copy(status = nextStatus)
                                    Toast.makeText(context, "Moved ${item.id} to $nextStatus", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OsMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun OsBoardItemCard(
    item: OsBoardItem,
    onPromoteStatus: () -> Unit
) {
    val typeColor = when (item.type) {
        "Bug" -> Color(0xFFFF5252)
        "Project" -> Color(0xFF448AFF)
        "Agent" -> Color(0xFF00E676)
        "Release" -> Color(0xFFFFAB40)
        "Build" -> Color(0xFFE040FB)
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = typeColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(item.type, fontSize = 9.sp, color = typeColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(item.priority, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(6.dp))
                    Text(item.id, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text(item.processId, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                }

                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("Assigned: ${item.ownerAgent} • Target: ${item.targetDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onPromoteStatus) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Advance Status", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
