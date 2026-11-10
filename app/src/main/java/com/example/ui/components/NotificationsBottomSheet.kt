package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppNotification
import com.example.data.local.NotificationManager
import com.example.data.local.NotificationType
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.LowStockRedContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onNavigateToItem: (NotificationType, String?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val notifications = NotificationManager.notifications
    val unreadCount = NotificationManager.unreadCount
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSlateSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = VividEmeraldPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isBangla) "নোটিফিকেশন ও অ্যালার্ট" else "Notifications & Alerts",
                            style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = TextPrimary
                        )
                        if (unreadCount > 0) {
                            Text(
                                text = if (isBangla) "$unreadCount টি নতুন অ্যালার্ট" else "$unreadCount new alerts",
                                style = AppTypographyScale.hintText,
                                color = VividEmeraldAccent
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notifications.isNotEmpty()) {
                        TextButton(
                            onClick = { NotificationManager.markAllAsRead(context) },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (isBangla) "সব পড়া হয়েছে" else "Mark all read",
                                style = AppTypographyScale.hintText.copy(color = VividEmeraldAccent)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            }

            // Notification List or Empty Text line
            // Notification List or Empty State
            if (notifications.isEmpty()) {
                FriendlyEmptyState(
                    icon = Icons.Default.Notifications,
                    title = if (isBangla) "বর্তমানে কোনো নোটিফিকেশন নেই" else "No Notifications Yet",
                    subtitle = if (isBangla) 
                        "বকেয়া পরিশোধ অ্যালার্ট এবং স্টক সতর্কবার্তা এখানে দেখতে পাবেন।" 
                    else 
                        "Overdue baki payment alerts and low stock warnings will appear here.",
                    actionText = if (isBangla) "সব ঠিক আছে" else "Close",
                    onActionClick = onDismiss,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        val isOverdue = notif.type == NotificationType.OVERDUE_PAYMENT

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (!notif.isRead) (if (isOverdue) LowStockRed.copy(0.6f) else VividEmeraldAccent.copy(0.6f)) else DarkSlateCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    NotificationManager.markAsRead(context, notif.id)
                                    onNavigateToItem(notif.type, notif.targetId)
                                    onDismiss()
                                }
                                .testTag("notification_item_${notif.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) DarkSlateSurfaceVariant else DarkSlateSurface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isOverdue) LowStockRedContainer else VividEmeraldPrimary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isOverdue) Icons.Default.Payment else Icons.Default.Inventory,
                                            contentDescription = null,
                                            tint = if (isOverdue) LowStockRed else VividEmeraldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBangla) notif.titleBn else notif.titleEn,
                                        style = AppTypographyScale.normalText.copy(
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isBangla) notif.messageBn else notif.messageEn,
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!notif.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isOverdue) LowStockRed else VividEmeraldAccent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
