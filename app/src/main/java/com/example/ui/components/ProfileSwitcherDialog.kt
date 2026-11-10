package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BusinessProfile
import com.example.data.local.PinLockManager
import com.example.data.local.ProfileManager
import com.example.ui.localization.AppLanguage
import com.example.ui.screens.setup.BUSINESS_TYPES_LIST
import com.example.ui.theme.AppTypographyScale
import com.example.ui.theme.DarkSlateCardBorder
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.LowStockRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividEmeraldAccent
import com.example.ui.theme.VividEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSwitcherBottomSheet(
    isBangla: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val profiles = ProfileManager.profiles
    val active = ProfileManager.activeProfile
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showCreateDialog by remember { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<BusinessProfile?>(null) }
    var showPinVerifyForDelete by remember { mutableStateOf(false) }

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
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = VividEmeraldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isBangla) "ব্যবসার প্রোফাইল নির্বাচন" else "Business Profiles",
                            style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = TextPrimary
                        )
                        Text(
                            text = if (isBangla) "এক অ্যাপে একাধিক ব্যবসার হিসাব পরিচালনা করুন" else "Manage multiple stores in one app",
                            style = AppTypographyScale.hintText,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            // Profiles List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(profiles, key = { it.id }) { p ->
                    val isActive = p.id == active?.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) VividEmeraldAccent else DarkSlateCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                ProfileManager.switchProfile(context, p.id)
                                Toast.makeText(
                                    context,
                                    if (isBangla) "${p.name} প্রোফাইলে পরিবর্তিত হয়েছে" else "Switched to ${p.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDismiss()
                            }
                            .testTag("profile_item_${p.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) DarkSlateSurfaceVariant else DarkSlateSurface
                        )
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
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isActive) VividEmeraldPrimary else DarkSlateSurfaceVariant,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Store,
                                            contentDescription = null,
                                            tint = if (isActive) Color.White else TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = p.name,
                                        style = AppTypographyScale.normalText.copy(fontWeight = FontWeight.Bold),
                                        color = if (isActive) VividEmeraldAccent else TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${p.businessType}${if (p.phone.isNotBlank()) " • ${p.phone}" else ""}",
                                        style = AppTypographyScale.hintText,
                                        color = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isActive) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = VividEmeraldPrimary.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, VividEmeraldAccent)
                                    ) {
                                        Text(
                                            text = if (isBangla) "সক্রিয়" else "Active",
                                            style = AppTypographyScale.hintText.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = VividEmeraldAccent,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                } else if (profiles.size > 1) {
                                    IconButton(
                                        onClick = {
                                            profileToDelete = p
                                            if (PinLockManager.isPinSet) {
                                                showPinVerifyForDelete = true
                                            } else {
                                                ProfileManager.deleteProfile(context, p.id)
                                                Toast.makeText(context, if (isBangla) "প্রোফাইল মুছে ফেলা হয়েছে" else "Profile deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Profile",
                                            tint = LowStockRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // "+ Create New Profile" Button
            OutlinedButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_create_new_profile"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, VividEmeraldAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VividEmeraldAccent)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "+ নতুন ব্যবসার প্রোফাইল যোগ করুন" else "+ Create New Business Profile",
                    style = AppTypographyScale.buttonText
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Create New Profile Dialog
    if (showCreateDialog) {
        CreateProfileDialog(
            isBangla = isBangla,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, phone, address, type ->
                ProfileManager.createProfile(context, name, phone, address, type)
                Toast.makeText(context, if (isBangla) "নতুন প্রোফাইল তৈরি হয়েছে!" else "New profile created!", Toast.LENGTH_SHORT).show()
                showCreateDialog = false
            }
        )
    }

    // PIN Verify Dialog for Profile Delete (Danger area PIN requirement)
    if (showPinVerifyForDelete && profileToDelete != null) {
        PinVerifyDialog(
            title = if (isBangla) "প্রোফাইল মুছতে পিন দিন" else "PIN required to delete profile",
            isBangla = isBangla,
            onDismiss = { showPinVerifyForDelete = false },
            onPinSuccess = {
                ProfileManager.deleteProfile(context, profileToDelete!!.id)
                showPinVerifyForDelete = false
                Toast.makeText(context, if (isBangla) "প্রোফাইল মুছে ফেলা হয়েছে" else "Profile deleted", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun CreateProfileDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onCreate: (name: String, phone: String, address: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(BUSINESS_TYPES_LIST[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নতুন ব্যবসা প্রোফাইল তৈরি" else "Create Business Profile",
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "দোকান / ব্যবসার নাম *" else "Store Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_name"),
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
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Phone Number") },
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
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBangla) "ঠিকানা" else "Address") },
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
                onClick = { onCreate(name, phone, address, selectedType) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("btn_save_new_profile")
            ) {
                Text(if (isBangla) "তৈরি করুন" else "Create")
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
fun EditProfileDialog(
    profile: BusinessProfile,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSave: (BusinessProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var phone by remember { mutableStateOf(profile.phone) }
    var address by remember { mutableStateOf(profile.address) }
    var regNumber by remember { mutableStateOf(profile.registrationNumber) }
    var bankAccount by remember { mutableStateOf(profile.bankAccount) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "প্রোফাইল তথ্য সম্পাদন" else "Edit Business Profile",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBangla) "দোকানের নাম *" else "Store Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_store_name"),
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
                    label = { Text(if (isBangla) "মোবাইল নম্বর" else "Mobile Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_phone"),
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
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBangla) "ঠিকানা / বাজার" else "Address / Market") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_address"),
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
                    value = regNumber,
                    onValueChange = { regNumber = it },
                    label = { Text(if (isBangla) "ট্রেড লাইসেন্স / রেজিঃ নং" else "Trade License / Reg No") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_reg_num"),
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
                    value = bankAccount,
                    onValueChange = { bankAccount = it },
                    label = { Text(if (isBangla) "ব্যাংক বা বিকাশ নম্বর" else "Bank Account / bKash") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_bank_account"),
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
                    onSave(
                        profile.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            registrationNumber = regNumber.trim(),
                            bankAccount = bankAccount.trim()
                        )
                    )
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("btn_save_edited_profile")
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}
