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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Customer
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
import java.util.Locale

@Composable
fun CustomersScreen(viewModel: BizlyViewModel) {
    val context = LocalContext.current
    val business by viewModel.business.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeTab by remember { mutableIntStateOf(0) } // 0: All, 1: Retail, 2: Wholesale, 3: Corporate, 4: Has Debt
    var selectedCustomerForDetail by remember { mutableStateOf<Customer?>(null) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filterTypes = listOf("All (${customers.size})", "Retail", "Wholesale", "Corporate", "With Debt (${customers.count { it.outstandingBalance > 0 }})")

    val filteredCustomers = customers.filter { cust ->
        val matchesType = when (selectedTypeTab) {
            1 -> cust.customerType == "Retail"
            2 -> cust.customerType == "Wholesale"
            3 -> cust.customerType == "Corporate"
            4 -> cust.outstandingBalance > 0
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                cust.name.contains(searchQuery, ignoreCase = true) ||
                cust.phone.contains(searchQuery, ignoreCase = true) ||
                cust.address.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customer Directory",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Total Credit Owed: $currency ${String.format(Locale.US, "%,.0f", customers.sumOf { it.outstandingBalance })}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("cust_add_btn")
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, phone, town...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = BizlyTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Type Chips
                ScrollableTabRow(
                    selectedTabIndex = selectedTypeTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    filterTypes.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTypeTab == index,
                            onClick = { selectedTypeTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTypeTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredCustomers) { cust ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedCustomerForDetail = cust }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BizlyPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cust.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = BizlyPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = cust.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = BizlyTextPrimary
                                    )
                                    Text(
                                        text = "${cust.phone} • ${cust.address}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BizlyTextSecondary
                                    )
                                }
                            }
                            StatusBadge(status = cust.customerType)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Purchases", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.0f", cust.totalPurchases)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Outstanding Debt", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    if (cust.outstandingBalance > 0) "$currency ${String.format(Locale.US, "%,.0f", cust.outstandingBalance)}" else "Zero Debt",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (cust.outstandingBalance > 0) BizlyWarning else BizlySuccess
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cust.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Call, "Call", tint = BizlySuccess, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${cust.phone.replace("+", "").replace(" ", "")}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Message, "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { customerToEdit = cust },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Edit, "Edit", tint = BizlyPrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { viewModel.deleteCustomer(cust) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = BizlyDanger, modifier = Modifier.size(18.dp))
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

    // Customer Detail Dialog (Purchase history, debt, quick note)
    selectedCustomerForDetail?.let { cust ->
        val customerSales = sales.filter { it.customerId == cust.id }
        val customerDebts = debts.filter { it.customerId == cust.id }

        AlertDialog(
            onDismissRequest = { selectedCustomerForDetail = null },
            title = {
                Column {
                    Text(cust.name, fontWeight = FontWeight.Bold)
                    Text(cust.customerType, style = MaterialTheme.typography.bodySmall, color = BizlyPrimary)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(340.dp)) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Phone: ${cust.phone}", style = MaterialTheme.typography.bodySmall)
                            if (cust.email.isNotBlank()) Text("Email: ${cust.email}", style = MaterialTheme.typography.bodySmall)
                            Text("Address: ${cust.address}", style = MaterialTheme.typography.bodySmall)
                            if (cust.notes.isNotBlank()) Text("Notes: ${cust.notes}", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (cust.outstandingBalance > 0) BizlyWarningLight else BizlySuccessLight)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Outstanding Balance: $currency ${String.format(Locale.US, "%,.2f", cust.outstandingBalance)}",
                                    color = if (cust.outstandingBalance > 0) BizlyWarning else BizlySuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Recent Transactions (${customerSales.size})", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                        }
                    }

                    items(customerSales) { sale ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(sale.saleNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Text(sale.paymentMethod, style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                            }
                            Text("$currency ${String.format(Locale.US, "%,.0f", sale.totalAmount)}", fontWeight = FontWeight.Bold, color = BizlyPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCustomerForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Add Customer Dialog
    if (showAddDialog) {
        CustomerFormDialog(
            title = "Add New Customer",
            initialCustomer = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, email, address, type, notes ->
                viewModel.addCustomer(name, phone, email, address, type, notes)
                showAddDialog = false
            }
        )
    }

    // Edit Customer Dialog
    customerToEdit?.let { cust ->
        CustomerFormDialog(
            title = "Edit Customer: ${cust.name}",
            initialCustomer = cust,
            onDismiss = { customerToEdit = null },
            onSave = { name, phone, email, address, type, notes ->
                viewModel.updateCustomer(
                    cust.copy(
                        name = name,
                        phone = phone,
                        email = email,
                        address = address,
                        customerType = type,
                        notes = notes
                    )
                )
                customerToEdit = null
            }
        )
    }
}

@Composable
fun CustomerFormDialog(
    title: String,
    initialCustomer: Customer?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, address: String, type: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(initialCustomer?.name ?: "") }
    var phone by remember { mutableStateOf(initialCustomer?.phone ?: "+254 7") }
    var email by remember { mutableStateOf(initialCustomer?.email ?: "") }
    var address by remember { mutableStateOf(initialCustomer?.address ?: "Nairobi") }
    var type by remember { mutableStateOf(initialCustomer?.customerType ?: "Retail") }
    var notes by remember { mutableStateOf(initialCustomer?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Location / Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Retail", "Wholesale", "Corporate").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Customer Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, phone, email, address, type, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Customer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
