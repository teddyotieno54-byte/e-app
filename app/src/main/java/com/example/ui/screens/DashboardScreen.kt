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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BizlyBarChart
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlyPrimaryLight
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlySuccessLight
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.theme.BizlyWarning
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: BizlyViewModel,
    onNavigate: (AppScreen) -> Unit,
    onOpenAddProductDialog: () -> Unit,
    onOpenAddCustomerDialog: () -> Unit,
    onOpenAddExpenseDialog: () -> Unit,
    onOpenCreateInvoiceDialog: () -> Unit
) {
    val business by viewModel.business.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val allSaleItems by viewModel.allSaleItems.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()
    val last7DaysMetrics by viewModel.last7DaysMetrics.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    // Time calculations
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
    val todayFormatted = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).format(Date())

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todaySales = sales.filter { it.createdAt >= todayStart }.sumOf { it.totalAmount }
    val todayExpenses = expenses.filter { it.dateMillis >= todayStart }.sumOf { it.amount }
    val todayProfit = todaySales - todayExpenses
    val totalOutstandingDebt = debts.filter { it.status != "Paid" }.sumOf { it.remainingAmount }
    val lowStockCount = products.count { it.status == "Low Stock" || it.status == "Out of Stock" }

    var selectedChartTab by remember { mutableIntStateOf(0) } // 0: Sales, 1: Expenses, 2: Profit

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // 1. Welcome Greeting Header
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, Kelvin",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = BizlyTextPrimary
                    )
                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = BizlyTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BizlyPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = business?.name ?: "TechPoint",
                        color = BizlyPrimary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Quick Action Buttons
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    label = "New Sale",
                    icon = Icons.Default.PointOfSale,
                    color = BizlySuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.SALES) }
                )
                QuickActionButton(
                    label = "+ Product",
                    icon = Icons.Default.Inventory2,
                    color = BizlyPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddProductDialog
                )
                QuickActionButton(
                    label = "+ Customer",
                    icon = Icons.Default.PersonAdd,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddCustomerDialog
                )
                QuickActionButton(
                    label = "+ Expense",
                    icon = Icons.Default.Paid,
                    color = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAddExpenseDialog
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. 6 Key Metric Cards (2x3 grid)
        item {
            Text(
                text = "Overview & Key Metrics",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "Today's Sales",
                    value = "$currency ${String.format(Locale.US, "%,.0f", todaySales)}",
                    icon = Icons.Default.PointOfSale,
                    subtitle = "${sales.count { it.createdAt >= todayStart }} transactions",
                    isPositiveTrend = true,
                    accentColor = BizlySuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Today's Expenses",
                    value = "$currency ${String.format(Locale.US, "%,.0f", todayExpenses)}",
                    icon = Icons.Default.TrendingDown,
                    subtitle = "Operational costs",
                    isPositiveTrend = false,
                    accentColor = BizlyDanger,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "Today's Net Profit",
                    value = "$currency ${String.format(Locale.US, "%,.0f", todayProfit)}",
                    icon = Icons.Default.TrendingUp,
                    subtitle = if (todayProfit >= 0) "Net gain today" else "Deficit today",
                    isPositiveTrend = todayProfit >= 0,
                    accentColor = if (todayProfit >= 0) BizlySuccess else BizlyDanger,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Outstanding Debts",
                    value = "$currency ${String.format(Locale.US, "%,.0f", totalOutstandingDebt)}",
                    icon = Icons.Default.CreditCard,
                    subtitle = "${debts.count { it.status == "Overdue" }} overdue",
                    accentColor = BizlyWarning,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "Low Stock Alerts",
                    value = "$lowStockCount items",
                    icon = Icons.Default.Warning,
                    subtitle = "${products.count { it.status == "Out of Stock" }} out of stock",
                    accentColor = if (lowStockCount > 0) BizlyDanger else BizlySuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Customers",
                    value = "${customers.size}",
                    icon = Icons.Default.People,
                    subtitle = "Active clients",
                    accentColor = BizlyPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 4. Financial Charts Section (Last 7 Days)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Performance Trend",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = BizlyTextPrimary
                            )
                            Text(
                                text = "Last 7 Days (KES)",
                                style = MaterialTheme.typography.bodySmall,
                                color = BizlyTextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = selectedChartTab == 0,
                                onClick = { selectedChartTab = 0 },
                                label = { Text("Sales", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedChartTab == 1,
                                onClick = { selectedChartTab = 1 },
                                label = { Text("Expenses", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedChartTab == 2,
                                onClick = { selectedChartTab = 2 },
                                label = { Text("Profit", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val labels = last7DaysMetrics.map { it.dayLabel }
                    val values = when (selectedChartTab) {
                        0 -> last7DaysMetrics.map { it.salesAmount }
                        1 -> last7DaysMetrics.map { it.expenseAmount }
                        else -> last7DaysMetrics.map { it.profitAmount.coerceAtLeast(0.0) }
                    }
                    val barColor = when (selectedChartTab) {
                        0 -> BizlyPrimary
                        1 -> BizlyDanger
                        else -> BizlySuccess
                    }

                    BizlyBarChart(
                        labels = labels,
                        values = values,
                        barColor = barColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 5. Top Selling Products
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top-Selling Products",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BizlyTextPrimary
                )
                Text(
                    text = "View inventory",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BizlyPrimary,
                    modifier = Modifier.clickable { onNavigate(AppScreen.INVENTORY) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Compute top sellers from allSaleItems
            val topSellers = allSaleItems
                .groupBy { it.productName }
                .map { (name, items) ->
                    val totalQty = items.sumOf { it.quantity }
                    val totalRev = items.sumOf { it.totalPrice }
                    Triple(name, totalQty, totalRev)
                }
                .sortedByDescending { it.second }
                .take(4)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                topSellers.forEachIndexed { index, (name, qty, rev) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BizlyPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BizlyPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = BizlyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$qty units sold",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BizlyTextSecondary
                                )
                            }
                            Text(
                                text = "$currency ${String.format(Locale.US, "%,.0f", rev)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = BizlySuccess
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 6. Recent Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BizlyTextPrimary
                )
                Text(
                    text = "View all sales",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BizlyPrimary,
                    modifier = Modifier.clickable { onNavigate(AppScreen.SALES) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val recentSales = sales.take(5)
        items(recentSales) { sale ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (sale.paymentMethod) {
                                    "M-PESA" -> BizlySuccessLight
                                    "Credit" -> Color(0xFFEDE9FE)
                                    else -> BizlyPrimary.copy(alpha = 0.1f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (sale.paymentMethod) {
                                "M-PESA" -> Icons.Default.PointOfSale
                                "Credit" -> Icons.Default.CreditCard
                                else -> Icons.Default.Receipt
                            },
                            contentDescription = null,
                            tint = when (sale.paymentMethod) {
                                "M-PESA" -> BizlySuccess
                                "Credit" -> Color(0xFF6D28D9)
                                else -> BizlyPrimary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sale.customerName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = BizlyTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(status = sale.paymentMethod)
                        }
                        Text(
                            text = "${sale.saleNumber} • ${SimpleDateFormat("d MMM, HH:mm", Locale.US).format(Date(sale.createdAt))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BizlyTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$currency ${String.format(Locale.US, "%,.0f", sale.totalAmount)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (sale.paymentMethod == "Credit") Color(0xFF6D28D9) else BizlyTextPrimary
                        )
                        if (sale.mpesaCode.isNotBlank()) {
                            Text(
                                text = sale.mpesaCode,
                                style = MaterialTheme.typography.labelSmall,
                                color = BizlySuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = BizlyTextPrimary,
                maxLines = 1
            )
        }
    }
}
