package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.model.Customer
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.BizlyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicesScreen(viewModel: BizlyViewModel) {
    val context = LocalContext.current
    val business by viewModel.business.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var selectedStatusIndex by remember { mutableIntStateOf(0) }
    var selectedInvoiceForPreview by remember { mutableStateOf<Invoice?>(null) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    val statusTabs = listOf("All (${invoices.size})", "Sent", "Paid", "Overdue", "Draft")

    val filteredInvoices = invoices.filter { inv ->
        when (selectedStatusIndex) {
            1 -> inv.status == "Sent"
            2 -> inv.status == "Paid"
            3 -> inv.status == "Overdue"
            4 -> inv.status == "Draft"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BizlyNavyDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Invoices & Billing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Total Invoiced: $currency ${String.format(Locale.US, "%,.0f", invoices.sumOf { it.totalAmount })}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = { showCreateInvoiceDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("invoice_create_btn")
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                ScrollableTabRow(
                    selectedTabIndex = selectedStatusIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    statusTabs.forEachIndexed { index, title ->
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

            items(filteredInvoices) { inv ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedInvoiceForPreview = inv }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = inv.customerName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                                Text(
                                    text = "${inv.invoiceNumber} • Due ${SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(inv.dueDate))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BizlyTextSecondary
                                )
                            }
                            StatusBadge(status = inv.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.2f", inv.totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyPrimary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (inv.status != "Paid") {
                                    OutlinedButton(
                                        onClick = { viewModel.updateInvoiceStatus(inv, "Paid") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = BizlySuccess)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Paid", fontSize = 11.sp, color = BizlySuccess)
                                    }
                                }
                                Button(
                                    onClick = { selectedInvoiceForPreview = inv },
                                    colors = ButtonDefaults.buttonColors(containerColor = BizlyNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview", fontSize = 11.sp)
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

    // Invoice Preview Dialog
    selectedInvoiceForPreview?.let { inv ->
        InvoicePreviewDialog(
            invoice = inv,
            currency = currency,
            businessName = business?.name ?: "TechPoint Electronics",
            businessLocation = business?.location ?: "Moi Avenue, Nairobi",
            businessPhone = business?.phone ?: "+254 712 345678",
            businessEmail = business?.email ?: "sales@techpoint-ke.com",
            tillNumber = business?.mpesaTillOrPaybill ?: "782910",
            onDismiss = { selectedInvoiceForPreview = null },
            onShare = {
                val shareBody = buildString {
                    appendLine("INVOICE: ${inv.invoiceNumber}")
                    appendLine("FROM: ${business?.name ?: "TechPoint Electronics"}")
                    appendLine("BILL TO: ${inv.customerName}")
                    appendLine("DUE DATE: ${SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date(inv.dueDate))}")
                    appendLine("AMOUNT DUE: $currency ${String.format(Locale.US, "%,.2f", inv.totalAmount)}")
                    appendLine("PAYMENT: M-PESA Till ${business?.mpesaTillOrPaybill ?: "782910"}")
                    appendLine("Powered by Bizly SaaS")
                }
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(intent, "Share Invoice via"))
            }
        )
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            customers = customers,
            onDismiss = { showCreateInvoiceDialog = false },
            onCreate = { customerId, name, email, phone, items, notes ->
                viewModel.createInvoice(customerId, name, email, phone, items, notes)
                showCreateInvoiceDialog = false
            }
        )
    }
}

@Composable
fun InvoicePreviewDialog(
    invoice: Invoice,
    currency: String,
    businessName: String,
    businessLocation: String,
    businessPhone: String,
    businessEmail: String,
    tillNumber: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Invoice ${invoice.invoiceNumber}", fontWeight = FontWeight.Bold)
                StatusBadge(status = invoice.status)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                item {
                    Text(text = businessName, fontWeight = FontWeight.Black, fontSize = 18.sp, color = BizlyNavyDark)
                    Text(text = "$businessLocation • Tel: $businessPhone", fontSize = 11.sp, color = BizlyTextSecondary)
                    Text(text = "Email: $businessEmail", fontSize = 11.sp, color = BizlyTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("BILL TO:", style = MaterialTheme.typography.labelSmall, color = BizlyTextMuted)
                    Text(invoice.customerName, fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                    if (invoice.customerPhone.isNotBlank()) Text("Phone: ${invoice.customerPhone}", fontSize = 12.sp, color = BizlyTextSecondary)
                    if (invoice.customerEmail.isNotBlank()) Text("Email: ${invoice.customerEmail}", fontSize = 12.sp, color = BizlyTextSecondary)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Issue Date:", style = MaterialTheme.typography.bodySmall)
                        Text(SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(invoice.issueDate)), style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Due Date:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(invoice.dueDate)), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                        Text("$currency ${String.format(Locale.US, "%,.2f", invoice.subtotal)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("VAT (16%)", style = MaterialTheme.typography.bodySmall)
                        Text("$currency ${String.format(Locale.US, "%,.2f", invoice.taxAmount)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL DUE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                        Text(
                            "$currency ${String.format(Locale.US, "%,.2f", invoice.totalAmount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = BizlyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Payment Instructions: Send M-PESA to Till #$tillNumber or Bank Transfer.\nNote: ${invoice.notes}",
                            fontSize = 11.sp,
                            color = BizlyNavyDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share / Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceDialog(
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onCreate: (customerId: Long, customerName: String, customerEmail: String, customerPhone: String, items: List<InvoiceItem>, notes: String) -> Unit
) {
    var selectedCustomerId by remember { mutableStateOf(customers.firstOrNull()?.id ?: 1L) }
    var itemDesc by remember { mutableStateOf("Electronics and Hardware Consignment") }
    var itemQty by remember { mutableStateOf("1") }
    var itemUnitPrice by remember { mutableStateOf("45000") }
    var notes by remember { mutableStateOf("Payment due within 14 days via M-PESA or Bank Transfer.") }
    var expandedCustomer by remember { mutableStateOf(false) }

    val selectedCust = customers.find { it.id == selectedCustomerId } ?: customers.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate New Invoice", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandedCustomer,
                    onExpandedChange = { expandedCustomer = !expandedCustomer }
                ) {
                    OutlinedTextField(
                        value = selectedCust?.name ?: "Select Customer",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Customer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCustomer) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCustomer,
                        onDismissRequest = { expandedCustomer = false }
                    ) {
                        customers.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.name} (${c.phone})") },
                                onClick = {
                                    selectedCustomerId = c.id
                                    expandedCustomer = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = itemDesc,
                    onValueChange = { itemDesc = it },
                    label = { Text("Item Description *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itemQty,
                        onValueChange = { itemQty = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = itemUnitPrice,
                        onValueChange = { itemUnitPrice = it },
                        label = { Text("Unit Price (KES)") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Terms & Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = itemQty.toIntOrNull() ?: 1
                    val price = itemUnitPrice.toDoubleOrNull() ?: 0.0
                    val items = listOf(
                        InvoiceItem(
                            invoiceId = 0,
                            description = itemDesc,
                            quantity = qty,
                            unitPrice = price,
                            amount = qty * price
                        )
                    )
                    if (selectedCust != null && itemDesc.isNotBlank()) {
                        onCreate(selectedCust.id, selectedCust.name, selectedCust.email, selectedCust.phone, items, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
