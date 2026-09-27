package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlyPrimaryContainer
import com.example.ui.theme.BizlyPrimaryLight
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlySuccessLight
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.theme.BizlyWarning
import com.example.ui.viewmodel.BizlyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SalesScreen(viewModel: BizlyViewModel) {
    val context = LocalContext.current
    val business by viewModel.business.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val selectedCustomerId by viewModel.selectedCustomerId.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val isCreditSale by viewModel.isCreditSale.collectAsStateWithLifecycle()
    val taxIncluded by viewModel.taxIncluded.collectAsStateWithLifecycle()
    val discountPercent by viewModel.discountPercent.collectAsStateWithLifecycle()
    val mpesaPhoneNumber by viewModel.mpesaPhoneNumber.collectAsStateWithLifecycle()
    val mpesaCodeInput by viewModel.mpesaCodeInput.collectAsStateWithLifecycle()
    val isStkPushing by viewModel.isStkPushing.collectAsStateWithLifecycle()
    val completedReceipt by viewModel.completedSaleReceipt.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    val categories = listOf("All") + products.map { it.category }.distinct()

    val filteredProducts = products.filter { prod ->
        val matchesCategory = selectedCategoryIndex == 0 || prod.category == categories[selectedCategoryIndex]
        val matchesSearch = searchQuery.isBlank() || prod.name.contains(searchQuery, ignoreCase = true) || prod.sku.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // Calculations
    val cartItems = cart.mapNotNull { (id, qty) ->
        products.find { it.id == id }?.let { it to qty }
    }
    val cartSubtotal = cartItems.sumOf { (prod, qty) -> prod.sellingPrice * qty }
    val discountAmount = cartSubtotal * (discountPercent / 100.0)
    val grandTotal = cartSubtotal - discountAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top POS Action Bar
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
                        text = "Point of Sale (POS)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${products.size} Products in Catalog",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                if (cart.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BizlySuccess)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${cart.values.sum()} in Cart • $currency ${String.format(Locale.US, "%,.0f", grandTotal)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            // Search and Category Bar
            item {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products or SKU...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = BizlyTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_product_search"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    categories.forEachIndexed { index, cat ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = { selectedCategoryIndex = index },
                            text = {
                                Text(
                                    text = cat,
                                    fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Products Grid / List
            items(filteredProducts) { product ->
                val inCartQty = cart[product.id] ?: 0
                val isOutOfStock = product.currentStock <= 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (inCartQty > 0) BizlyPrimaryLight else BizlyBorder
                        )
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${product.sku} • Stock: ${product.currentStock}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (product.currentStock <= 3) BizlyDanger else BizlyTextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(status = product.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currency ${String.format(Locale.US, "%,.2f", product.sellingPrice)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = BizlyPrimary
                            )
                        }

                        // Add / Stepper controls
                        if (isOutOfStock) {
                            Text(
                                text = "Sold Out",
                                color = BizlyDanger,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        } else if (inCartQty == 0) {
                            Button(
                                onClick = { viewModel.addToCart(product) },
                                colors = ButtonDefaults.buttonColors(containerColor = BizlyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("pos_add_item_${product.id}")
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BizlyPrimaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(product.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Remove, null, tint = BizlyPrimary)
                                }
                                Text(
                                    text = "$inCartQty",
                                    fontWeight = FontWeight.Bold,
                                    color = BizlyPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.addToCart(product) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, null, tint = BizlyPrimary)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom Cart Summary & Checkout Tray
        if (cart.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Customer & Discount Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val selectedCustomer = customers.find { it.id == selectedCustomerId }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showCustomerPicker = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = BizlyPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedCustomer?.name ?: "Walk-in Customer (Change)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BizlyTextPrimary
                            )
                        }

                        // Discount dropdown / chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0, 5, 10).forEach { disc ->
                                FilterChip(
                                    selected = discountPercent == disc,
                                    onClick = { viewModel.setDiscountPercent(disc) },
                                    label = { Text(if (disc == 0) "No Disc" else "-$disc%", fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Payment Method selector
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BizlyTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("M-PESA", "Cash", "Card", "Bank", "Credit").forEach { method ->
                            val isSelected = paymentMethod == method
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) {
                                            if (method == "M-PESA") BizlySuccess else BizlyPrimary
                                        } else Color(0xFFF1F5F9)
                                    )
                                    .clickable { viewModel.setPaymentMethod(method) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else BizlyTextPrimary
                                )
                            }
                        }
                    }

                    // M-PESA STK Push Simulation Section
                    if (paymentMethod == "M-PESA") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BizlySuccessLight)
                                .border(1.dp, BizlySuccess.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PhoneAndroid, null, tint = BizlySuccess, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "M-PESA STK Push Ready",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = BizlySuccess
                                        )
                                    }
                                    if (isStkPushing) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BizlySuccess, strokeWidth = 2.dp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = mpesaPhoneNumber,
                                        onValueChange = { viewModel.setMpesaPhoneNumber(it) },
                                        label = { Text("Customer Phone") },
                                        modifier = Modifier.weight(1.3f),
                                        singleLine = true
                                    )
                                    Button(
                                        onClick = { viewModel.triggerMpesaStkPush(grandTotal) },
                                        enabled = !isStkPushing,
                                        colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("pos_stk_push_btn")
                                    ) {
                                        Text(if (isStkPushing) "Sending..." else "STK Push", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (mpesaCodeInput.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Confirmed Code: $mpesaCodeInput",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = BizlySuccess
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Checkout Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                            Text(
                                text = "$currency ${String.format(Locale.US, "%,.2f", grandTotal)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = BizlyTextPrimary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.clearCart() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Clear")
                            }

                            Button(
                                onClick = { viewModel.checkout(products, customers) },
                                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("pos_complete_checkout_btn")
                            ) {
                                Text("Complete Sale", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Customer Picker Dialog
    if (showCustomerPicker) {
        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            title = { Text("Select Customer", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(260.dp)) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedCustomer(null)
                                    showCustomerPicker = false
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Walk-in Customer (General)", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    items(customers) { cust ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedCustomer(cust.id)
                                    showCustomerPicker = false
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cust.name, fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                                    Text(cust.phone, style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                                }
                                if (cust.outstandingBalance > 0) {
                                    Text("Debt: KES ${cust.outstandingBalance.toInt()}", color = BizlyWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Professional Receipt Modal (Requirement 8)
    completedReceipt?.let { (sale, items) ->
        ReceiptDialog(
            sale = sale,
            items = items,
            currency = currency,
            businessName = business?.name ?: "TechPoint Electronics",
            businessLocation = business?.location ?: "Moi Avenue, Nairobi",
            businessPhone = business?.phone ?: "+254 712 345678",
            onDismiss = { viewModel.dismissReceipt() },
            onShare = {
                val receiptText = buildString {
                    appendLine("================================")
                    appendLine("       ${business?.name ?: "TechPoint Electronics"}")
                    appendLine("  ${business?.location ?: "Moi Avenue, Nairobi"}")
                    appendLine("  Tel: ${business?.phone ?: "+254 712 345678"}")
                    appendLine("================================")
                    appendLine("Receipt: ${sale.saleNumber}")
                    appendLine("Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date(sale.createdAt))}")
                    appendLine("Customer: ${sale.customerName}")
                    appendLine("Served By: ${sale.employeeName}")
                    appendLine("--------------------------------")
                    items.forEach {
                        appendLine("${it.productName} x${it.quantity} = $currency ${it.totalPrice}")
                    }
                    appendLine("--------------------------------")
                    appendLine("Subtotal: $currency ${sale.subtotal}")
                    if (sale.discountAmount > 0) appendLine("Discount: -$currency ${sale.discountAmount}")
                    appendLine("TOTAL: $currency ${sale.totalAmount}")
                    appendLine("Payment: ${sale.paymentMethod}")
                    if (sale.mpesaCode.isNotBlank()) appendLine("M-PESA Code: ${sale.mpesaCode}")
                    appendLine("================================")
                    appendLine("    Run your business smarter    ")
                    appendLine("       Powered by Bizly SaaS    ")
                }
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, receiptText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Receipt via"))
            }
        )
    }
}

@Composable
fun ReceiptDialog(
    sale: Sale,
    items: List<SaleItem>,
    currency: String,
    businessName: String,
    businessLocation: String,
    businessPhone: String,
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
                Text("Sale Receipt", fontWeight = FontWeight.Bold)
                StatusBadge(status = sale.paymentStatus)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = businessName,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = BizlyNavyDark
                        )
                        Text(
                            text = businessLocation,
                            fontSize = 11.sp,
                            color = BizlyTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tel: $businessPhone",
                            fontSize = 11.sp,
                            color = BizlyTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Receipt #:", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                        Text(sale.saleNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date:", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                        Text(
                            SimpleDateFormat("d MMM yyyy, HH:mm", Locale.US).format(Date(sale.createdAt)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Customer:", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                        Text(sale.customerName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cashier:", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                        Text(sale.employeeName, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = BizlyTextPrimary
                            )
                            Text(
                                text = "${item.quantity} x $currency ${String.format(Locale.US, "%,.2f", item.unitPrice)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BizlyTextSecondary
                            )
                        }
                        Text(
                            text = "$currency ${String.format(Locale.US, "%,.2f", item.totalPrice)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                        Text("$currency ${String.format(Locale.US, "%,.2f", sale.subtotal)}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (sale.discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount", style = MaterialTheme.typography.bodySmall, color = BizlySuccess)
                            Text("-$currency ${String.format(Locale.US, "%,.2f", sale.discountAmount)}", style = MaterialTheme.typography.bodySmall, color = BizlySuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                        Text(
                            "$currency ${String.format(Locale.US, "%,.2f", sale.totalAmount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = BizlyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payment Method:", style = MaterialTheme.typography.bodySmall)
                        Text(sale.paymentMethod, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    if (sale.mpesaCode.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("M-PESA Code:", style = MaterialTheme.typography.bodySmall, color = BizlySuccess)
                            Text(sale.mpesaCode, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = BizlySuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Thank you for shopping with us!\nRun your business smarter with Bizly SaaS.",
                        fontSize = 11.sp,
                        color = BizlyTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
