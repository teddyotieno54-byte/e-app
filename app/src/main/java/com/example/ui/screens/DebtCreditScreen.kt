package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Debt
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlySuccessLight
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.theme.BizlyWarning
import com.example.ui.theme.BizlyWarningLight
import com.example.ui.viewmodel.BizlyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtCreditScreen(viewModel: BizlyViewModel) {
    val context = LocalContext.current
    val business by viewModel.business.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var selectedStatusIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Overdue, 2: Partially Paid, 3: Pending, 4: Paid
    var debtForRepayment by remember { mutableStateOf<Debt?>(null) }
    var debtForReminder by remember { mutableStateOf<Debt?>(null) }

    val filterOptions = listOf(
        "All (${debts.size})",
        "Overdue (${debts.count { it.status == "Overdue" }})",
        "Partially Paid (${debts.count { it.status == "Partially Paid" }})",
        "Pending (${debts.count { it.status == "Pending" }})",
        "Settled (${debts.count { it.status == "Paid" }})"
    )

    val filteredDebts = debts.filter { debt ->
        when (selectedStatusIndex) {
            1 -> debt.status == "Overdue"
            2 -> debt.status == "Partially Paid"
            3 -> debt.status == "Pending"
            4 -> debt.status == "Paid"
            else -> true
        }
    }

    val totalOutstanding = debts.filter { it.status != "Paid" }.sumOf { it.remainingAmount }
    val totalCollected = debts.sumOf { it.paidAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BizlyNavyDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Text(
                    text = "Debt & Credit Ledger",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Unpaid", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text(
                            text = "$currency ${String.format(Locale.US, "%,.0f", totalOutstanding)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = BizlyWarning
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Collected", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text(
                            text = "$currency ${String.format(Locale.US, "%,.0f", totalCollected)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BizlySuccess
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Status Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedStatusIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    filterOptions.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedStatusIndex == index,
                            onClick = { selectedStatusIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedStatusIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredDebts) { debt ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = debt.customerName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                                Text(
                                    text = "${debt.customerPhone} • Due ${SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(debt.dueDate))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (debt.status == "Overdue") BizlyDanger else BizlyTextSecondary
                                )
                            }
                            StatusBadge(status = debt.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Amount statistics
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Credit", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.0f", debt.totalAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BizlyTextPrimary
                                )
                            }
                            Column {
                                Text("Amount Paid", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.0f", debt.paidAmount)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlySuccess
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining Balance", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.0f", debt.remainingAmount)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (debt.remainingAmount > 0) BizlyWarning else BizlySuccess
                                )
                            }
                        }

                        if (debt.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Note: ${debt.notes}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BizlyTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Record Payment, Send Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (debt.remainingAmount > 0) {
                                OutlinedButton(
                                    onClick = { debtForReminder = debt },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Send, null, modifier = Modifier.size(14.dp), tint = Color(0xFF0284C7))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Reminder", fontSize = 11.sp, color = Color(0xFF0284C7))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { debtForRepayment = debt },
                                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("debt_record_pay_${debt.id}")
                                ) {
                                    Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Payment", fontSize = 11.sp)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = BizlySuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Fully Settled", color = BizlySuccess, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Record Repayment Dialog
    debtForRepayment?.let { debt ->
        RecordPaymentDialog(
            debt = debt,
            currency = currency,
            onDismiss = { debtForRepayment = null },
            onConfirm = { amount, method, ref ->
                viewModel.recordDebtPayment(debt.id, amount, method, ref)
                debtForRepayment = null
            }
        )
    }

    // Send Reminder Dialog
    debtForReminder?.let { debt ->
        val reminderMessage = "Dear ${debt.customerName}, this is a gentle reminder from ${business?.name ?: "TechPoint Electronics"} regarding your outstanding balance of $currency ${String.format(Locale.US, "%,.2f", debt.remainingAmount)} due on ${SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(debt.dueDate))}. Please make payments via ${business?.mpesaType ?: "Till"}: ${business?.mpesaTillOrPaybill ?: "782910"}. Thank you!"

        AlertDialog(
            onDismissRequest = { debtForReminder = null },
            title = { Text("Payment Reminder Template", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Customer: ${debt.customerName} (${debt.customerPhone})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Text(text = reminderMessage, style = MaterialTheme.typography.bodySmall, color = BizlyTextPrimary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reminderMessage)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Send Reminder via"))
                        debtForReminder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Message, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send via WhatsApp/SMS")
                }
            },
            dismissButton = {
                TextButton(onClick = { debtForReminder = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RecordPaymentDialog(
    debt: Debt,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, reference: String) -> Unit
) {
    var amountString by remember { mutableStateOf(debt.remainingAmount.toInt().toString()) }
    var method by remember { mutableStateOf("M-PESA") }
    var reference by remember { mutableStateOf("QK" + (100000..999999).random() + "KE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Debt Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Customer: ${debt.customerName}\nRemaining Owed: $currency ${String.format(Locale.US, "%,.2f", debt.remainingAmount)}",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Payment Amount ($currency) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("M-PESA", "Cash", "Bank").forEach { m ->
                        FilterChip(
                            selected = method == m,
                            onClick = { method = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Payment Reference / Receipt #") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountString.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(amt, method, reference)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm Repayment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
