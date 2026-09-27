package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.data.model.StockAdjustment
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.theme.BizlyWarning
import com.example.ui.viewmodel.BizlyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: BizlyViewModel) {
    val business by viewModel.business.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val stockAdjustments by viewModel.stockAdjustments.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusTab by remember { mutableIntStateOf(0) } // 0: All, 1: Low Stock, 2: Out of Stock, 3: In Stock
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productForStockAdjust by remember { mutableStateOf<Product?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    val statusTabs = listOf("All (${products.size})", "Low Stock (${products.count { it.status == "Low Stock" }})", "Out of Stock (${products.count { it.status == "Out of Stock" }})", "In Stock (${products.count { it.status == "In Stock" }})")

    val filteredProducts = products.filter { prod ->
        val matchesStatus = when (selectedStatusTab) {
            1 -> prod.status == "Low Stock"
            2 -> prod.status == "Out of Stock"
            3 -> prod.status == "In Stock"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.sku.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
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
                        text = "Inventory & Products",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Total Valuation: $currency ${String.format(Locale.US, "%,.0f", products.sumOf { it.currentStock * it.buyingPrice })}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { showHistoryDialog = true }) {
                        Icon(Icons.Default.History, "Stock History", tint = Color.White)
                    }
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("inventory_add_product_btn")
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by product name, SKU or category...") },
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

                // Status Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedStatusTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    statusTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedStatusTab == index,
                            onClick = { selectedStatusTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedStatusTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredProducts) { prod ->
                val margin = if (prod.sellingPrice > 0) {
                    ((prod.sellingPrice - prod.buyingPrice) / prod.sellingPrice) * 100
                } else 0.0

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
                                    text = prod.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${prod.sku} • ${prod.category} • Supplier: ${prod.supplier}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BizlyTextSecondary
                                )
                            }
                            StatusBadge(status = prod.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Price and Stock Stats
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Selling Price", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.2f", prod.sellingPrice)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyPrimary
                                )
                            }
                            Column {
                                Text("Buying Price", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "$currency ${String.format(Locale.US, "%,.2f", prod.buyingPrice)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BizlyTextPrimary
                                )
                            }
                            Column {
                                Text("Margin", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "${margin.toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlySuccess
                                )
                            }
                            Column {
                                Text("In Stock", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(
                                    "${prod.currentStock} units",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (prod.currentStock <= prod.minStockLevel) BizlyDanger else BizlyTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions: Adjust Stock, Edit, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { productForStockAdjust = prod },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Tune, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Adjust Stock", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { productToEdit = prod },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Edit, null, tint = BizlyPrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { viewModel.deleteProduct(prod) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = BizlyDanger, modifier = Modifier.size(18.dp))
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

    // Add Product Dialog
    if (showAddDialog) {
        ProductFormDialog(
            title = "Add New Product",
            initialProduct = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, sku, category, buying, selling, stock, minStock, supplier, desc ->
                viewModel.addProduct(name, sku, category, buying, selling, stock, minStock, supplier, desc)
                showAddDialog = false
            }
        )
    }

    // Edit Product Dialog
    productToEdit?.let { prod ->
        ProductFormDialog(
            title = "Edit Product: ${prod.name}",
            initialProduct = prod,
            onDismiss = { productToEdit = null },
            onSave = { name, sku, category, buying, selling, stock, minStock, supplier, desc ->
                viewModel.updateProduct(
                    prod.copy(
                        name = name,
                        sku = sku,
                        category = category,
                        buyingPrice = buying,
                        sellingPrice = selling,
                        currentStock = stock,
                        minStockLevel = minStock,
                        supplier = supplier,
                        description = desc
                    )
                )
                productToEdit = null
            }
        )
    }

    // Stock Adjustment Dialog
    productForStockAdjust?.let { prod ->
        StockAdjustDialog(
            product = prod,
            onDismiss = { productForStockAdjust = null },
            onConfirm = { type, qty, reason ->
                viewModel.adjustStock(prod.id, type, qty, reason)
                productForStockAdjust = null
            }
        )
    }

    // Stock History Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = { Text("Stock Audit & Adjustment Log", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(350.dp)) {
                    items(stockAdjustments) { adj ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(adj.productName, fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                                    StatusBadge(status = adj.type)
                                }
                                Text(
                                    text = "${if (adj.quantityChange > 0) "+" else ""}${adj.quantityChange} units (${adj.previousStock} → ${adj.newStock})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (adj.quantityChange >= 0) BizlySuccess else BizlyDanger
                                )
                                Text(
                                    text = "${adj.reason} • ${SimpleDateFormat("d MMM, HH:mm", Locale.US).format(Date(adj.createdAt))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BizlyTextSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    title: String,
    initialProduct: Product?,
    onDismiss: () -> Unit,
    onSave: (name: String, sku: String, category: String, buying: Double, selling: Double, stock: Int, minStock: Int, supplier: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var sku by remember { mutableStateOf(initialProduct?.sku ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "Smartphones") }
    var buyingPrice by remember { mutableStateOf(initialProduct?.buyingPrice?.toString() ?: "") }
    var sellingPrice by remember { mutableStateOf(initialProduct?.sellingPrice?.toString() ?: "") }
    var stock by remember { mutableStateOf(initialProduct?.currentStock?.toString() ?: "10") }
    var minStock by remember { mutableStateOf(initialProduct?.minStockLevel?.toString() ?: "5") }
    var supplier by remember { mutableStateOf(initialProduct?.supplier ?: "Supplier Kenya") }
    var desc by remember { mutableStateOf(initialProduct?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prod_form_name_input"),
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("SKU / Barcode") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = buyingPrice,
                            onValueChange = { buyingPrice = it },
                            label = { Text("Buying Price (KES)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            label = { Text("Selling Price (KES) *") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("prod_form_price_input"),
                            singleLine = true
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Current Stock *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Min Alert Level") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Supplier Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Product Description / Specs") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name,
                            sku,
                            category,
                            buyingPrice.toDoubleOrNull() ?: 0.0,
                            sellingPrice.toDoubleOrNull() ?: 0.0,
                            stock.toIntOrNull() ?: 0,
                            minStock.toIntOrNull() ?: 3,
                            supplier,
                            desc
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("prod_form_save_btn")
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StockAdjustDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (type: String, qtyChange: Int, reason: String) -> Unit
) {
    var adjustType by remember { mutableStateOf("Stock In") } // Stock In, Stock Out, Damaged, Recount
    var qtyString by remember { mutableStateOf("5") }
    var reason by remember { mutableStateOf("Supplier restock delivery") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adjust Stock: ${product.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Current stock: ${product.currentStock} units", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Stock In", "Stock Out", "Damaged", "Recount").forEach { type ->
                        FilterChip(
                            selected = adjustType == type,
                            onClick = {
                                adjustType = type
                                reason = when (type) {
                                    "Stock In" -> "Supplier purchase shipment"
                                    "Stock Out" -> "Transfer to secondary branch"
                                    "Damaged" -> "Display demo wear and tear"
                                    else -> "Periodic inventory recount audit"
                                }
                            },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = qtyString,
                    onValueChange = { qtyString = it },
                    label = { Text(if (adjustType == "Recount") "Exact New Count" else "Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inputVal = qtyString.toIntOrNull() ?: 0
                    val change = if (adjustType == "Recount") {
                        inputVal - product.currentStock
                    } else if (adjustType == "Stock In") {
                        inputVal
                    } else {
                        -inputVal
                    }
                    onConfirm(adjustType, change, reason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BizlyPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply Adjustment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
