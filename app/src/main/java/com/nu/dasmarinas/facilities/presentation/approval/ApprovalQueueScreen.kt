package com.nu.dasmarinas.facilities.presentation.approval

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nu.dasmarinas.facilities.domain.model.Reservation
import com.nu.dasmarinas.facilities.domain.model.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalQueueScreen(
    pendingReservations: List<Reservation>,
    onBackClick: () -> Unit,
    onReservationClick: (String) -> Unit,
    onApproveClick: (String) -> Unit,
    onRejectClick: (String) -> Unit,
    onRequestChangesClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Approval Queue",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${pendingReservations.size} pending approval(s)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by event or organization...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Priority Filter
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Filter by Priority",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedPriority == null,
                            onClick = { selectedPriority = null },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = selectedPriority == Priority.HIGH,
                            onClick = { selectedPriority = Priority.HIGH },
                            label = { Text("High") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFEBEE),
                                selectedLabelColor = Color(0xFFD32F2F)
                            )
                        )
                        FilterChip(
                            selected = selectedPriority == Priority.MEDIUM,
                            onClick = { selectedPriority = Priority.MEDIUM },
                            label = { Text("Medium") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFF8E1),
                                selectedLabelColor = Color(0xFFF57C00)
                            )
                        )
                        FilterChip(
                            selected = selectedPriority == Priority.NORMAL,
                            onClick = { selectedPriority = Priority.NORMAL },
                            label = { Text("Normal") }
                        )
                    }
                }
            }

            // Priority Order Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Priority Order",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PriorityItem("1. High:", "University Events", Color(0xFFD32F2F))
                    PriorityItem("2. Medium:", "Academic Classes", Color(0xFFF57C00))
                    PriorityItem("3. Normal:", "Student Organization Events", Color(0xFF1976D2))
                }
            }

            // Approval Queue List
            val filteredReservations = pendingReservations
                .filter { reservation ->
                    (searchQuery.isEmpty() || 
                     reservation.eventTitle.contains(searchQuery, ignoreCase = true) ||
                     reservation.organizer.organization.contains(searchQuery, ignoreCase = true)) &&
                    (selectedPriority == null || reservation.priority == selectedPriority)
                }
                .sortedWith(
                    compareByDescending<Reservation> { 
                        when(it.priority) {
                            Priority.HIGH -> 3
                            Priority.MEDIUM -> 2
                            Priority.NORMAL -> 1
                        }
                    }.thenBy { it.submittedAt }
                )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReservations) { reservation ->
                    ApprovalQueueCard(
                        reservation = reservation,
                        onClick = { onReservationClick(reservation.id) },
                        onApprove = { onApproveClick(reservation.id) },
                        onReject = { onRejectClick(reservation.id) },
                        onRequestChanges = { onRequestChangesClick(reservation.id) }
                    )
                }

                if (filteredReservations.isEmpty()) {
                    item {
                        EmptyQueueMessage()
                    }
                }
            }

            // Note at bottom
            if (pendingReservations.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note:\nPlease manually check the calendar for scheduling conflicts before approving. If a conflict exists, reject the request and the system will suggest alternative dates to the requestor.",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityItem(number: String, text: String, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF424242)
        )
    }
}

@Composable
private fun ApprovalQueueCard(
    reservation: Reservation,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRequestChanges: () -> Unit
) {
    val priorityColor = when (reservation.priority) {
        Priority.HIGH -> Color(0xFFD32F2F)
        Priority.MEDIUM -> Color(0xFFF57C00)
        Priority.NORMAL -> Color(0xFF1976D2)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Priority Badge and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = priorityColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = priorityColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${reservation.priority.name} Priority",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = priorityColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reservation.eventTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                }
                Text(
                    text = reservation.eventType.name.replace("_", " "),
                    fontSize = 12.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Organization
            Text(
                text = reservation.organizer.organization,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            Text(
                text = "Requested by: ${reservation.organizer.name} (${reservation.organizer.idNumber})",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailChip(
                    icon = Icons.Default.LocationOn,
                    text = reservation.facility.name
                )
                DetailChip(
                    icon = Icons.Default.People,
                    text = "${reservation.expectedAttendees}\nattendees"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                DetailChip(
                    icon = Icons.Default.CalendarToday,
                    text = "${reservation.date}\n${reservation.startTime} - ${reservation.endTime}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Submitted ${formatDate(reservation.submittedAt)}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onRequestChanges,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Request\nChanges", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF424242),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun EmptyQueueMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "All caught up!",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "No pending approvals",
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return "Feb 1"
}
