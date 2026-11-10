package com.example.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BusinessProfile
import com.example.ui.components.HardwareCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary

/**
 * 3. "Complete Your Profile" progress card showing completion percentage until filled in
 */
@Composable
fun ProfileCompletionCard(
    activeProfile: BusinessProfile?,
    isBangla: Boolean,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percent = activeProfile?.getCompletionPercentage() ?: 20
    if (percent >= 100) return // Hide when 100% complete

    val missingFields = activeProfile?.getMissingFields(isBangla) ?: emptyList()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, VividEmeraldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onCompleteClick() }
            .testTag("profile_completion_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBangla) "দোকানের প্রোফাইল সম্পন্ন করুন" else "Complete Your Profile",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VividEmeraldPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$percent% " + (if (isBangla) "সম্পন্ন" else "Done"),
                        style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                        color = VividEmeraldAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = VividEmeraldAccent,
                trackColor = DarkSlateSurfaceVariant
            )

            if (missingFields.isNotEmpty()) {
                Text(
                    text = (if (isBangla) "বাকি তথ্য: " else "Add missing: ") + missingFields.take(2).joinToString(", "),
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VividEmeraldPrimary
                ) {
                    Text(
                        text = if (isBangla) "তথ্য পূরণ করুন ➔" else "Fill Details ➔",
                        style = AppTypographyScale.buttonText.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

/**
 * 18. Sales, Purchase and Expense cards for the period (tappable to open details)
 */
@Composable
fun PeriodFiguresSection(
    figures: PeriodFigures,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onSalesClick: () -> Unit = {},
    onPurchasesClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "এই মাসের হিসাব (This Month)" else "This Month's Summary",
                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sales Card
            PeriodMiniCard(
                title = if (isBangla) "মোট বিক্রি" else "Sales",
                amount = figures.sales,
                icon = Icons.Default.TrendingUp,
                accentColor = VividEmeraldAccent,
                currentLanguage = currentLanguage,
                onClick = onSalesClick,
                modifier = Modifier.weight(1f)
            )

            // Purchase Card
            PeriodMiniCard(
                title = if (isBangla) "মোট ক্রয়" else "Purchases",
                amount = figures.purchases,
                icon = Icons.Default.ShoppingCart,
                accentColor = Color(0xFF38BDF8),
                currentLanguage = currentLanguage,
                onClick = onPurchasesClick,
                modifier = Modifier.weight(1f)
            )

            // Expense Card
            PeriodMiniCard(
                title = if (isBangla) "দোকান খরচ" else "Expenses",
                amount = figures.expenses,
                icon = Icons.Default.TrendingDown,
                accentColor = Color(0xFFF87171),
                currentLanguage = currentLanguage,
                onClick = onExpensesClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PeriodMiniCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    accentColor: Color,
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = formatTaka(amount, currentLanguage, isMaskable = true),
                style = AppTypographyScale.moneyNumber.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 19. Total Balance card for cash and bank
 */
@Composable
fun TotalBalanceCard(
    balance: Double,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("total_balance_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = VividEmeraldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isBangla) "মোট ব্যালেন্স (ক্যাশ ও ব্যাংক)" else "Total Balance (Cash & Bank)",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                    Text(
                        text = formatTaka(balance, currentLanguage, isMaskable = true),
                        style = AppTypographyScale.moneyNumber.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = VividEmeraldAccent
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSlateSurfaceVariant
            ) {
                Text(
                    text = if (isBangla) "হিসাব ➔" else "Details ➔",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.SemiBold),
                    color = VividEmeraldAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 23. Cashflow chart for the last 7 days with period switch
 */
@Composable
fun CashflowChartCard(
    chartState: CashflowChartState,
    currentLanguage: AppLanguage,
    isBangla: Boolean,
    onChartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var periodSwitch by remember { mutableStateOf("7days") } // "7days" or "today"

    HardwareCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cashflow_chart_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isBangla) "ক্যাশফ্লো প্রবাহ (টাকা আসা ও যাওয়া)" else "Cashflow (Money In & Out)",
                        style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = if (isBangla) "গত ৭ দিনের নগদ লেনদেন চিত্র" else "7-Day cash inflow vs outflow",
                        style = AppTypographyScale.hintText,
                        color = TextMuted
                    )
                }

                // Period Switch
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSlateSurfaceVariant)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { periodSwitch = "7days" },
                        color = if (periodSwitch == "7days") VividEmeraldPrimary else Color.Transparent
                    ) {
                        Text(
                            text = if (isBangla) "৭ দিন" else "7 Days",
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = if (periodSwitch == "7days") Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { periodSwitch = "today" },
                        color = if (periodSwitch == "today") VividEmeraldPrimary else Color.Transparent
                    ) {
                        Text(
                            text = if (isBangla) "আজ" else "Daily",
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = if (periodSwitch == "today") Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Summary row: Total In vs Total Out
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VividEmeraldAccent))
                        Text(
                            text = (if (isBangla) "জমা (In): " else "In: ") + formatTaka(chartState.totalIn, currentLanguage, isMaskable = true),
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                            color = VividEmeraldAccent
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF87171)))
                        Text(
                            text = (if (isBangla) "খরচ (Out): " else "Out: ") + formatTaka(chartState.totalOut, currentLanguage, isMaskable = true),
                            style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF87171)
                        )
                    }
                }

                Text(
                    text = (if (isBangla) "নীট: " else "Net: ") + formatTaka(chartState.netCashflow, currentLanguage, isMaskable = true),
                    style = AppTypographyScale.hintText.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (chartState.netCashflow >= 0) VividEmeraldAccent else LowStockRed
                    )
                )
            }

            // 7-day Bar Visualization
            val maxAmount = chartState.days.maxOfOrNull { maxOf(it.moneyIn, it.moneyOut) }?.coerceAtLeast(100.0) ?: 100.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSlateSurfaceVariant.copy(alpha = 0.5f))
                    .clickable { onChartClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                chartState.days.forEach { day ->
                    val inHeightRatio = (day.moneyIn / maxAmount).toFloat().coerceIn(0.08f, 1f)
                    val outHeightRatio = (day.moneyOut / maxAmount).toFloat().coerceIn(0.08f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(54.dp)
                        ) {
                            // Money In Bar (Green)
                            Box(
                                modifier = Modifier
                                    .width(7.dp)
                                    .fillMaxHeight(inHeightRatio)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(VividEmeraldAccent)
                            )
                            // Money Out Bar (Red)
                            Box(
                                modifier = Modifier
                                    .width(7.dp)
                                    .fillMaxHeight(outHeightRatio)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(Color(0xFFF87171))
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = day.dayName,
                            style = AppTypographyScale.hintText.copy(fontSize = 10.sp),
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 24. Explore App row: Quick Entry, Quick POS, View Reports
 */
@Composable
fun ExploreAppRow(
    isBangla: Boolean,
    onQuickEntryClick: () -> Unit,
    onQuickPosClick: () -> Unit,
    onViewReportsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isBangla) "অ্যাপ এক্সপ্লোর করুন" else "Explore App",
            style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExploreTile(
                title = if (isBangla) "কুইক এন্ট্রি" else "Quick Entry",
                subtitle = if (isBangla) "টাকা জমা/খরচ" else "Instant Ledger",
                icon = Icons.Default.Receipt,
                color = VividEmeraldPrimary,
                onClick = onQuickEntryClick,
                modifier = Modifier.weight(1f)
            )

            ExploreTile(
                title = if (isBangla) "কুইক পিওএস" else "Quick POS",
                subtitle = if (isBangla) "কাউন্টার সেল" else "Fast Billing",
                icon = Icons.Default.PointOfSale,
                color = Color(0xFF2563EB),
                onClick = onQuickPosClick,
                modifier = Modifier.weight(1f)
            )

            ExploreTile(
                title = if (isBangla) "রিপোর্ট দেখুন" else "View Reports",
                subtitle = if (isBangla) "লাভের হিসাব" else "Profit & Chart",
                icon = Icons.Default.Assessment,
                color = Color(0xFFD97706),
                onClick = onViewReportsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ExploreTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = AppTypographyScale.hintText.copy(fontSize = 11.sp),
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 25. Customizable shortcuts grid with an Edit Menu (tick, untick, reorder)
 */
@Composable
fun ShortcutsGridSection(
    shortcuts: List<HomeShortcutItem>,
    isBangla: Boolean,
    onShortcutClick: (HomeShortcutItem) -> Unit,
    onToggleShortcut: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditMenuDialog by remember { mutableStateOf(false) }
    val enabledShortcuts = shortcuts.filter { it.isEnabled }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isBangla) "প্রয়োজনীয় শর্টকাট" else "Shortcuts",
                style = AppTypographyScale.sectionHeading.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSlateSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showEditMenuDialog = true }
                    .testTag("btn_edit_shortcuts")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isBangla) "মেনু পরিবর্তন" else "Edit Menu",
                        style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = VividEmeraldAccent
                    )
                }
            }
        }

        // 4-column shortcuts grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            enabledShortcuts.forEach { shortcut ->
                Surface(
                    onClick = { onShortcutClick(shortcut) },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSlateSurface,
                    border = BorderStroke(1.dp, DarkSlateCardBorder),
                    modifier = Modifier
                        .width(78.dp)
                        .height(82.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VividEmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getShortcutIcon(shortcut.iconName),
                                    contentDescription = null,
                                    tint = VividEmeraldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBangla) shortcut.titleBn else shortcut.titleEn,
                            style = AppTypographyScale.hintText.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showEditMenuDialog) {
        EditShortcutsDialog(
            shortcuts = shortcuts,
            isBangla = isBangla,
            onDismiss = { showEditMenuDialog = false },
            onToggle = onToggleShortcut
        )
    }
}

@Composable
private fun EditShortcutsDialog(
    shortcuts: List<HomeShortcutItem>,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onToggle: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "হোম শর্টকাট কাস্টমাইজ করুন" else "Customize Home Shortcuts",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "যে শর্টকাটগুলো হোমে দেখতে চান সেগুলো টিক দিন:"
                    else
                        "Select shortcuts to display on the Home screen grid:",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                shortcuts.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggle(item.id) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = getShortcutIcon(item.iconName),
                                contentDescription = null,
                                tint = if (item.isEnabled) VividEmeraldAccent else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBangla) item.titleBn else item.titleEn,
                                style = AppTypographyScale.normalText,
                                color = if (item.isEnabled) TextPrimary else TextMuted
                            )
                        }

                        Checkbox(
                            checked = item.isEnabled,
                            onCheckedChange = { onToggle(item.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = VividEmeraldPrimary,
                                uncheckedColor = DarkSlateCardBorder
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "সম্পন্ন" else "Done")
            }
        }
    )
}

/**
 * 26. Promotional banner area for tips or videos that I can edit
 */
@Composable
fun PromotionalBannerCard(
    title: String,
    subtitle: String,
    isBangla: Boolean,
    onSaveBanner: (title: String, subtitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditBannerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, VividEmeraldAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .testTag("promotional_banner_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = AppTypographyScale.hintText.copy(fontSize = 12.sp),
                    color = TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { showEditBannerDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Banner",
                    tint = VividEmeraldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showEditBannerDialog) {
        EditBannerDialog(
            currentTitle = title,
            currentSubtitle = subtitle,
            isBangla = isBangla,
            onDismiss = { showEditBannerDialog = false },
            onSave = { newTitle, newSubtitle ->
                onSaveBanner(newTitle, newSubtitle)
                showEditBannerDialog = false
            }
        )
    }
}

@Composable
private fun EditBannerDialog(
    currentTitle: String,
    currentSubtitle: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }
    var subtitle by remember { mutableStateOf(currentSubtitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "টিপস ও ব্যানার সম্পাদন" else "Edit Store Tips & Banner",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBangla) "শিরোনাম" else "Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text(if (isBangla) "বিবরণ বা ভিডিও লিংক" else "Tips Message or Link") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = VividEmeraldPrimary,
                        unfocusedBorderColor = DarkSlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, subtitle) },
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

private fun getShortcutIcon(iconName: String): ImageVector {
    return when (iconName) {
        "person_add" -> Icons.Default.PersonAdd
        "receipt" -> Icons.Default.Receipt
        "payment_in" -> Icons.Default.Payment
        "payment_out" -> Icons.Default.TrendingDown
        "shopping_cart" -> Icons.Default.ShoppingCart
        "add_box" -> Icons.Default.AddBox
        "money_off" -> Icons.Default.MoneyOff
        "note_add" -> Icons.Default.NoteAdd
        "account_balance" -> Icons.Default.AccountBalance
        "assignment_return" -> Icons.Default.AssignmentReturn
        "reply" -> Icons.Default.Reply
        "description" -> Icons.Default.Description
        else -> Icons.Default.Add
    }
}
