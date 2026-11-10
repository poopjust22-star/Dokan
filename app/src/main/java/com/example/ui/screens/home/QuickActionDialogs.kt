package com.example.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Customer
import com.example.data.local.entity.Product
import com.example.data.local.entity.Supplier
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
 * Quick Action Dialogs for Home Screen Shortcuts
 */

@Composable
fun QuickAddPartyDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSaveCustomer: (name: String, phone: String, address: String, initialBaki: Double) -> Unit,
    onSaveSupplier: (name: String, company: String, phone: String, amountOwed: Double) -> Unit
) {
    var partyType by remember { mutableStateOf("customer") } // "customer" or "supplier"
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressOrCompany by remember { mutableStateOf("") }
    var initialAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নতুন পার্টি যোগ করুন" else "Add New Party",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Toggle Customer vs Supplier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { partyType = "customer" },
                        color = if (partyType == "customer") VividEmeraldPrimary else DarkSlateSurfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isBangla) "গ্রাহক (Customer)" else "Customer",
                                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                                color = if (partyType == "customer") androidx.compose.ui.graphics.Color.White else TextMuted
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { partyType = "supplier" },
                        color = if (partyType == "supplier") VividEmeraldPrimary else DarkSlateSurfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isBangla) "মহাজন (Supplier)" else "Supplier",
                                style = AppTypographyScale.hintText.copy(fontWeight = FontWeight.Bold),
                                color = if (partyType == "supplier") androidx.compose.ui.graphics.Color.White else TextMuted
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "নাম *" else "Name *") },
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
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Phone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    value = addressOrCompany,
                    onValueChange = { addressOrCompany = it },
                    label = {
                        Text(
                            if (partyType == "customer") (if (isBangla) "ঠিকানা" else "Address")
                            else (if (isBangla) "কোম্পানির নাম / প্রতিষ্ঠান" else "Company / Business Name")
                        )
                    },
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
                    value = initialAmount,
                    onValueChange = { initialAmount = it },
                    label = {
                        Text(
                            if (partyType == "customer") (if (isBangla) "পূর্বের বাকি পাওনা (৳)" else "Previous Due (৳)")
                            else (if (isBangla) "মহাজন দেনা (৳)" else "Amount Owed (৳)")
                        )
                    },
                    placeholder = { Text("0") },
                    prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                onClick = {
                    val amount = initialAmount.toDoubleOrNull() ?: 0.0
                    if (partyType == "customer") {
                        onSaveCustomer(name, phone, addressOrCompany, amount)
                    } else {
                        onSaveSupplier(name, addressOrCompany, phone, amount)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save Party")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun QuickPaymentInDialog(
    customers: List<Customer>,
    isBangla: Boolean,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (customerId: Long, amount: Double, note: String) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(customers.firstOrNull { it.totalBaki > 0 } ?: customers.firstOrNull()) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isDropdownOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "পেমেন্ট ইন (টাকা জমা)" else "Payment In (Receive Payment)",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Customer selector
                Text(
                    text = if (isBangla) "গ্রাহক নির্বাচন করুন" else "Select Customer",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isDropdownOpen = true },
                        color = DarkSlateSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedCustomer?.let { "${it.name} (বাকি: ৳${it.totalBaki.toLong()})" }
                                    ?: (if (isBangla) "গ্রাহক নির্বাচন করুন" else "Select Customer"),
                                style = AppTypographyScale.normalText,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text("▼", color = TextMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = isDropdownOpen,
                        onDismissRequest = { isDropdownOpen = false },
                        modifier = Modifier.background(DarkSlateSurface)
                    ) {
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${cust.name} - বাকি: ৳${cust.totalBaki.toLong()}",
                                        color = TextPrimary
                                    )
                                },
                                onClick = {
                                    selectedCustomer = cust
                                    isDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isBangla) "জমার পরিমাণ (৳) *" else "Amount Received (৳) *") },
                    prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "নোট / রসিদ বিবরণ" else "Note / Receipt Ref") },
                    placeholder = { Text("e.g. Cash payment") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cust = selectedCustomer ?: return@Button
                    val amt = amountText.toDoubleOrNull() ?: return@Button
                    if (amt > 0) {
                        onConfirm(cust.id, amt, note)
                    }
                },
                enabled = selectedCustomer != null && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "জমা নিশ্চিত করুন" else "Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun QuickSalesReturnDialog(
    products: List<Product>,
    customers: List<Customer>,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (productId: Long, qty: Int, refundAmount: Double, customerId: Long?, note: String) -> Unit
) {
    var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var returnQtyText by remember { mutableStateOf("1") }
    var refundAmountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isProductDropdownOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "বিক্রয় ফেরত (Sales Return)" else "Sales Return (Credit Note)",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "ফেরতকৃত পণ্যটি স্টকে ফেরত যোগ হবে এবং কাস্টমার বাকি বা ক্যাশ রিফান্ড সমন্বয় হবে।"
                    else
                        "Returned goods are restored to inventory stock and customer due is adjusted.",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                // Select product
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isProductDropdownOpen = true },
                        color = DarkSlateSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedProduct?.localizedName(isBangla) ?: if (isBangla) "পণ্য নির্বাচন করুন" else "Select Product",
                                color = TextPrimary
                            )
                            Text("▼", color = TextMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = isProductDropdownOpen,
                        onDismissRequest = { isProductDropdownOpen = false },
                        modifier = Modifier.background(DarkSlateSurface)
                    ) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.localizedName(isBangla), color = TextPrimary) },
                                onClick = {
                                    selectedProduct = p
                                    isProductDropdownOpen = false
                                    val q = returnQtyText.toIntOrNull() ?: 1
                                    refundAmountText = (p.sellPrice * q).toLong().toString()
                                }
                            )
                        }
                    }
                }

                // Return quantity
                OutlinedTextField(
                    value = returnQtyText,
                    onValueChange = {
                        returnQtyText = it
                        val q = it.toIntOrNull() ?: 1
                        val price = selectedProduct?.sellPrice ?: 0.0
                        refundAmountText = (price * q).toLong().toString()
                    },
                    label = { Text(if (isBangla) "ফেরতের পরিমাণ" else "Return Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Refund Amount
                OutlinedTextField(
                    value = refundAmountText,
                    onValueChange = { refundAmountText = it },
                    label = { Text(if (isBangla) "ফেরত মূল্য (৳)" else "Refund Amount (৳)") },
                    prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isBangla) "ফেরতের কারণ / নোট" else "Reason / Note") },
                    placeholder = { Text("e.g. Defective or customer exchange") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = selectedProduct ?: return@Button
                    val q = returnQtyText.toIntOrNull() ?: 1
                    val amt = refundAmountText.toDoubleOrNull() ?: (p.sellPrice * q)
                    onConfirm(p.id, q, amt, selectedCustomer?.id, note)
                },
                enabled = selectedProduct != null && (returnQtyText.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "ফেরত সম্পন্ন করুন" else "Process Return")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun QuickQuotationDialog(
    products: List<Product>,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConvertToSale: (product: Product, qty: Int, price: Double, customerName: String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }
    var qtyText by remember { mutableStateOf("1") }
    var quotePriceText by remember { mutableStateOf(selectedProduct?.sellPrice?.toLong()?.toString() ?: "") }
    var isProductDropdownOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "দরপত্র / কোটেশন তৈরি (Quotation)" else "Create Price Quotation",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "কোটেশন তৈরি করলে স্টকের কোনো পরিবর্তন হয় না। প্রয়োজন অনুযায়ী পরবর্তীতে এটিকে সরাসরি চালানে রূপান্তর করা যাবে।"
                    else
                        "Quotations do not reduce stock. Can be converted directly to sale when buyer confirms.",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(if (isBangla) "গ্রাহকের নাম / প্রতিষ্ঠান *" else "Customer / Company *") },
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

                // Select product
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isProductDropdownOpen = true },
                        color = DarkSlateSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedProduct?.localizedName(isBangla) ?: if (isBangla) "পণ্য নির্বাচন" else "Select Item",
                                color = TextPrimary
                            )
                            Text("▼", color = TextMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = isProductDropdownOpen,
                        onDismissRequest = { isProductDropdownOpen = false },
                        modifier = Modifier.background(DarkSlateSurface)
                    ) {
                        products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.localizedName(isBangla), color = TextPrimary) },
                                onClick = {
                                    selectedProduct = p
                                    isProductDropdownOpen = false
                                    quotePriceText = p.sellPrice.toLong().toString()
                                }
                            )
                        }
                    }
                }

                // Quantity
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text(if (isBangla) "পরিমাণ" else "Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Price
                OutlinedTextField(
                    value = quotePriceText,
                    onValueChange = { quotePriceText = it },
                    label = { Text(if (isBangla) "প্রস্তাবিত দর (৳)" else "Quoted Unit Price (৳)") },
                    prefix = { Text("৳ ", color = VividEmeraldAccent, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                onClick = {
                    val p = selectedProduct ?: return@Button
                    val q = qtyText.toIntOrNull() ?: 1
                    val price = quotePriceText.toDoubleOrNull() ?: p.sellPrice
                    onConvertToSale(p, q, price, customerName.ifBlank { "Walk-in Buyer" })
                },
                enabled = selectedProduct != null && (qtyText.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary)
            ) {
                Text(if (isBangla) "বিক্রিতে রূপান্তর করুন" else "Convert to Sale")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}
