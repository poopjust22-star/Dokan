package com.example.ui.screens.sell

import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.Customer
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.formatTaka
import com.example.ui.localization.toBengaliNumerals
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Multi-product sales Cart Checkout Bottom Sheet (Requirements 32, 33, 34, 35, 36, 40).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutBottomSheet(
    viewModel: SellViewModel,
    isBangla: Boolean,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()

    var discountText by remember { mutableStateOf("") }
    var isCreditSale by remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var paidNowText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var isCustomerMenuExpanded by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val pickedName = if (nameIndex >= 0) it.getString(nameIndex) else ""
                        if (pickedName.isNotBlank()) {
                            viewModel.addNewCustomer(pickedName, "", "")
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, if (isBangla) "কন্টাক্ট পড়া যায়নি" else "Failed to read contact", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val subtotal = cartItems.sumOf { it.total }
    val enteredDiscount = discountText.toDoubleOrNull() ?: 0.0
    // Requirement 32: discount must never be bigger than price, total never below zero
    val discount = enteredDiscount.coerceIn(0.0, subtotal)
    val totalAmount = (subtotal - discount).coerceAtLeast(0.0)

    val now = remember { System.currentTimeMillis() }
    val invoiceNum = remember { "INV-${(now % 900000) + 100000}" }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSlateSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Invoice & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (isBangla) "বিক্রয় কার্ট (${cartItems.size})" else "Sales Cart (${cartItems.size})",
                        style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            // Invoice & Date Subheader (Requirement 35)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isBangla) "চালান নং: " else "Invoice: "}$invoiceNum",
                    style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                    color = VividEmeraldAccent
                )
                Text(
                    text = "${if (isBangla) "তারিখ: " else "Date: "}${dateFormat.format(Date(now))}",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )
            }

            Divider(color = DarkSlateCardBorder)

            // Items List in Cart (Requirement 34)
            Text(
                text = if (isBangla) "নির্বাচিত পণ্যসমূহ" else "Selected Items",
                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            cartItems.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceVariant),
                    border = BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.localizedName(isBangla),
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "প্রতি ${item.product.localizedUnit(isBangla)} ৳${item.unitPrice.toLong()} • মজুদ: ${item.product.stockQuantity}",
                                style = AppTypographyScale.hintText,
                                color = TextMuted
                            )
                        }

                        // Stepper [-] Qty [+]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.updateCartItemQuantity(item.product.id, -1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "${item.quantity}",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = { viewModel.updateCartItemQuantity(item.product.id, 1) },
                                modifier = Modifier.size(32.dp),
                                enabled = item.quantity < item.product.stockQuantity
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = VividEmeraldAccent, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { viewModel.removeFromCart(item.product.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = LowStockRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Party Selection: Cash Sale as default or Customer (Requirement 33)
            Text(
                text = if (isBangla) "ক্রেতা / পার্টি নির্বাচন (ডিফল্ট: ক্যাশ বিক্রি)" else "Customer / Party (Default: Cash Sale)",
                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, DarkSlateCardBorder, RoundedCornerShape(10.dp))
                        .clickable { isCustomerMenuExpanded = true }
                        .testTag("cart_customer_selector"),
                    color = DarkSlateSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedCustomer != null) VividEmeraldAccent else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = selectedCustomer?.let { "${it.name} (${it.phone.ifBlank { "No phone" }})" }
                                    ?: if (isBangla) "ক্যাশ বিক্রি (সাধারণ ক্রেতা)" else "Cash Sale (Walk-in Customer)",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        Text(text = "▼", style = AppTypographyScale.hintText, color = TextMuted)
                    }
                }

                DropdownMenu(
                    expanded = isCustomerMenuExpanded,
                    onDismissRequest = { isCustomerMenuExpanded = false },
                    modifier = Modifier.background(DarkSlateSurface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isBangla) "ক্যাশ বিক্রি (সাধারণ ক্রেতা)" else "Cash Sale (Walk-in Customer)",
                                style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                color = VividEmeraldAccent
                            )
                        },
                        onClick = {
                            selectedCustomer = null
                            isCustomerMenuExpanded = false
                        }
                    )
                    allCustomers.forEach { cust ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(cust.name, style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Medium), color = TextPrimary)
                                    if (cust.phone.isNotBlank()) {
                                        Text(cust.phone, style = AppTypographyScale.hintText, color = TextMuted)
                                    }
                                }
                            },
                            onClick = {
                                selectedCustomer = cust
                                isCustomerMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Pick from contacts or Add customer buttons (Requirement 33)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { contactPickerLauncher.launch(null) },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, VividEmeraldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VividEmeraldAccent)
                ) {
                    Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isBangla) "কন্টাক্টস থেকে" else "Contacts", style = AppTypographyScale.buttonText.copy(fontSize = 12.sp))
                }

                OutlinedButton(
                    onClick = { viewModel.openNewCustomerDialog() },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, VividEmeraldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VividEmeraldAccent)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isBangla) "+ নতুন গ্রাহক" else "+ New Customer", style = AppTypographyScale.buttonText.copy(fontSize = 12.sp))
                }
            }

            // Optional Discount Row (Clamped per Requirement 32)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "ছাড় / ডিসকাউন্ট (ঐচ্ছিক)" else "Discount (Optional)",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = discountText,
                    onValueChange = { discountText = it.filter { c -> c.isDigit() || c == '.' } },
                    modifier = Modifier.width(140.dp).heightIn(min = 44.dp),
                    placeholder = { Text("0", color = TextMuted) },
                    prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
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

            // Notes / Remarks field (Requirement 36)
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(if (isBangla) "মন্তব্য বা বিবরণ (ঐচ্ছিক)" else "Notes / Remarks (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSlateSurfaceVariant,
                    unfocusedContainerColor = DarkSlateSurfaceVariant,
                    focusedBorderColor = VividEmeraldPrimary,
                    unfocusedBorderColor = DarkSlateCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Total Amount Card (Requirement 34)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurfaceVariant),
                border = BorderStroke(1.dp, VividEmeraldAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "সর্বমোট প্রদেয় বিল" else "Total Payable Bill",
                            style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "সাবটোটাল ৳${subtotal.toLong()}" + if (discount > 0) " - ছাড় ৳${discount.toLong()}" else "",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = formatTaka(totalAmount, currentLanguage),
                        style = AppTypographyScale.moneyNumber.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividEmeraldAccent
                        )
                    )
                }
            }

            // Payment Type: Cash vs Credit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isCreditSale = false },
                    shape = RoundedCornerShape(10.dp),
                    color = if (!isCreditSale) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                    border = if (!isCreditSale) null else BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (!isCreditSale) Color.White else TextMuted, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBangla) "নগদ পরিশোধ" else "Paid in Full", style = AppTypographyScale.buttonText, color = if (!isCreditSale) Color.White else TextMuted)
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isCreditSale = true },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCreditSale) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                    border = if (isCreditSale) null else BorderStroke(1.dp, DarkSlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = if (isCreditSale) Color.White else TextMuted, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBangla) "বাকি (ক্রেডিট)" else "Baki (Credit)", style = AppTypographyScale.buttonText, color = if (isCreditSale) Color.White else TextMuted)
                    }
                }
            }

            // If Credit: Partial Payment
            AnimatedVisibility(visible = isCreditSale) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = paidNowText,
                        onValueChange = { paidNowText = it.filter { c -> c.isDigit() || c == '.' } },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        label = { Text(if (isBangla) "নগদ জমা / অগ্রিম পরিশোধ (৳)" else "Paid Now / Down Payment (৳)") },
                        placeholder = { Text("0", color = TextMuted) },
                        prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSlateSurfaceVariant,
                            unfocusedContainerColor = DarkSlateSurfaceVariant,
                            focusedBorderColor = VividEmeraldPrimary,
                            unfocusedBorderColor = DarkSlateCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    val enteredPaid = paidNowText.toDoubleOrNull() ?: 0.0
                    val remainingDue = (totalAmount - enteredPaid).coerceAtLeast(0.0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isBangla) "গ্রাহকের বাকি থাকবে:" else "Remaining Due:", style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold), color = LowStockRed)
                        Text(formatTaka(remainingDue, currentLanguage), style = AppTypographyScale.moneyNumber.copy(fontWeight = FontWeight.Bold, color = LowStockRed))
                    }
                }
            }

            // Confirm Sale Button
            val canConfirm = cartItems.isNotEmpty() && (!isCreditSale || selectedCustomer != null)
            Button(
                onClick = {
                    val enteredPaid = paidNowText.toDoubleOrNull() ?: 0.0
                    viewModel.confirmCartSale(
                        isBangla = isBangla,
                        customer = selectedCustomer,
                        rawDiscount = discount,
                        isCredit = isCreditSale,
                        paidNow = enteredPaid,
                        notes = notesText,
                        onSuccess = { onDismiss() }
                    )
                },
                enabled = canConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_cart_confirm_sale"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(
                    text = if (isBangla) "অর্ডার সম্পন্ন করুন (Confirm Order)" else "Confirm Order",
                    style = AppTypographyScale.buttonText.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }

            // Clear Cart Button
            TextButton(
                onClick = {
                    viewModel.clearCart()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text(if (isBangla) "কার্ট খালি করুন" else "Clear Cart", style = AppTypographyScale.buttonText, color = LowStockRed)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
