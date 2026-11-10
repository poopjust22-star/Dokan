package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PinLockManager
import com.example.ui.localization.AppLanguage
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
 * Dialog to verify PIN before executing sensitive actions.
 */
@Composable
fun PinVerifyDialog(
    title: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onPinSuccess: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        icon = {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = VividEmeraldPrimary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = VividEmeraldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                style = AppTypographyScale.screenTitle.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla) "সংবেদনশীল কাজের জন্য আপনার নিরাপত্তা পিন দিন" else "Enter security PIN to continue sensitive action",
                    style = AppTypographyScale.hintText,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pinText,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                            pinText = it
                            isError = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_pin_verify"),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    textStyle = AppTypographyScale.screenTitle.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 8.sp,
                        color = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSlateSurfaceVariant,
                        unfocusedContainerColor = DarkSlateSurfaceVariant,
                        focusedBorderColor = if (isError) LowStockRed else VividEmeraldPrimary,
                        unfocusedBorderColor = if (isError) LowStockRed else DarkSlateCardBorder
                    )
                )

                if (isError) {
                    Text(
                        text = if (isBangla) "ভুল পিন কোড! আবার চেষ্টা করুন।" else "Incorrect PIN! Please try again.",
                        style = AppTypographyScale.hintText,
                        color = LowStockRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (PinLockManager.verifyPin(pinText)) {
                        onPinSuccess()
                    } else {
                        isError = true
                    }
                },
                enabled = pinText.length >= 4,
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("btn_pin_confirm")
            ) {
                Text(if (isBangla) "যাচাই করুন" else "Verify", style = AppTypographyScale.buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}

/**
 * Dialog to Set or Change Security PIN.
 */
@Composable
fun PinSetupDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onPinSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSlateSurface,
        title = {
            Text(
                text = if (isBangla) "নিরাপত্তা পিন সেট করুন" else "Set Security PIN",
                style = AppTypographyScale.screenTitle,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "তথ্য মোছা, ব্যাকআপ রিস্টোর বা সংবেদনশীল কাজ সুরক্ষায় ৪-৬ ডিজিটের পিন ব্যবহার করুন।"
                    else
                        "Set a 4-6 digit PIN to protect sensitive actions like data deletion and restore.",
                    style = AppTypographyScale.hintText,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) newPin = it },
                    label = { Text(if (isBangla) "নতুন পিন (৪-৬ সংখ্যা)" else "New PIN (4-6 digits)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_new_pin"),
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

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) confirmPin = it },
                    label = { Text(if (isBangla) "পিন নিশ্চিত করুন" else "Confirm PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_confirm_pin"),
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

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = AppTypographyScale.hintText,
                        color = LowStockRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin.length < 4) {
                        errorMessage = if (isBangla) "পিন কমপক্ষে ৪ সংখ্যার হতে হবে" else "PIN must be at least 4 digits"
                    } else if (newPin != confirmPin) {
                        errorMessage = if (isBangla) "দুটো পিন মেলেনি!" else "PINs do not match!"
                    } else {
                        PinLockManager.setPin(context, newPin)
                        onPinSaved()
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VividEmeraldPrimary),
                modifier = Modifier.testTag("btn_save_pin")
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
