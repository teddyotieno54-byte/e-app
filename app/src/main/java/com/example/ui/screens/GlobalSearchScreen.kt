package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel
import java.util.Locale

@Composable
fun GlobalSearchScreen(
    viewModel: BizlyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val employees by viewModel.employees.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }

    val matchedProducts = if (query.isNotBlank()) products.filter {
        it.name.contains(query, ignoreCase = true) || it.sku.contains(query, ignoreCase = true)
    } else emptyList()

    val matchedCustomers = if (query.isNotBlank()) customers.filter {
        it.name.contains(query, ignoreCase = true) || it.phone.contains(query, ignoreCase = true)
    } else emptyList()

    val matchedSales = if (query.isNotBlank()) sales.filter {
        it.saleNumber.contains(query, ignoreCase = true) || it.customerName.contains(query, ignoreCase = true) || it.mpesaCode.contains(query, ignoreCase = true)
    } else emptyList()

    val matchedInvoices = if (query.isNotBlank()) invoices.filter {
        it.invoiceNumber.contains(query, ignoreCase = true) || it.customerName.contains(query, ignoreCase = true)
    } else emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BizlyNavyDark)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search customers, products, sales, M-PESA...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = BizlyPrimary) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (query.isBlank()) {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Search across your entire business", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                        Text("Type a product name, customer phone, M-PESA code, or receipt number.", fontSize = 12.sp, color = BizlyTextSecondary)
                    }
                }
            } else {
                // Products
                if (matchedProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Products (${matchedProducts.size})", fontWeight = FontWeight.Bold, color = BizlyPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedProducts) { prod ->
                        SearchResultCard(
                            title = prod.name,
                            subtitle = "${prod.sku} • Stock: ${prod.currentStock} units • KES ${prod.sellingPrice}",
                            icon = Icons.Default.Inventory2,
                            badge = prod.status,
                            onClick = { onNavigate(AppScreen.INVENTORY) }
                        )
                    }
                }

                // Customers
                if (matchedCustomers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Customers (${matchedCustomers.size})", fontWeight = FontWeight.Bold, color = BizlyPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedCustomers) { cust ->
                        SearchResultCard(
                            title = cust.name,
                            subtitle = "${cust.phone} • ${cust.address} • Purchases: KES ${cust.totalPurchases.toInt()}",
                            icon = Icons.Default.Person,
                            badge = cust.customerType,
                            onClick = { onNavigate(AppScreen.CUSTOMERS) }
                        )
                    }
                }

                // Sales
                if (matchedSales.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Sales & Receipts (${matchedSales.size})", fontWeight = FontWeight.Bold, color = BizlyPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedSales) { sale ->
                        SearchResultCard(
                            title = "${sale.saleNumber} • ${sale.customerName}",
                            subtitle = "KES ${sale.totalAmount} • ${sale.paymentMethod} ${if (sale.mpesaCode.isNotBlank()) "(${sale.mpesaCode})" else ""}",
                            icon = Icons.Default.PointOfSale,
                            badge = sale.paymentStatus,
                            onClick = { onNavigate(AppScreen.SALES) }
                        )
                    }
                }

                // Invoices
                if (matchedInvoices.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Invoices (${matchedInvoices.size})", fontWeight = FontWeight.Bold, color = BizlyPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedInvoices) { inv ->
                        SearchResultCard(
                            title = "${inv.invoiceNumber} • ${inv.customerName}",
                            subtitle = "Total: KES ${inv.totalAmount}",
                            icon = Icons.Default.Receipt,
                            badge = inv.status,
                            onClick = { onNavigate(AppScreen.INVOICES) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BizlyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BizlyPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
            }
            StatusBadge(status = badge)
        }
    }
}
