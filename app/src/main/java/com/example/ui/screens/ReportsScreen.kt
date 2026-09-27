package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BizlyBarChart
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.theme.BizlyWarning
import com.example.ui.viewmodel.BizlyViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun ReportsScreen(viewModel: BizlyViewModel) {
    val context = LocalContext.current
    val business by viewModel.business.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val debts by viewModel.debts.collectAsStateWithLifecycle()
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val last7DaysMetrics by viewModel.last7DaysMetrics.collectAsStateWithLifecycle()

    val currency = business?.currency ?: "KES"

    var selectedPeriodIndex by remember { mutableIntStateOf(2) } // 0: Today, 1: This Week, 2: This Month, 3: All Time
    val periodTabs = listOf("Today", "This Week", "This Month", "All Time")

    // Filter by period
    val now = System.currentTimeMillis()
    val startTime = when (selectedPeriodIndex) {
        0 -> Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        1 -> now - 7 * 86400000L
        2 -> now - 30 * 86400000L
        else -> 0L
    }

    val periodSales = sales.filter { it.createdAt >= startTime }
    val periodExpenses = expenses.filter { it.dateMillis >= startTime }

    val totalRevenue = periodSales.sumOf { it.totalAmount }
    val totalExpense = periodExpenses.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpense
    val profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0

    val inventoryValuation = products.sumOf { it.currentStock * it.buyingPrice }
    val potentialRevenue = products.sumOf { it.currentStock * it.sellingPrice }
    val expectedGrossProfit = potentialRevenue - inventoryValuation

    val totalOutstandingDebt = debts.filter { it.status != "Paid" }.sumOf { it.remainingAmount }

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
                        text = "Business Intelligence & Reports",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "P&L, Margins & Cash Flow",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = {
                        val reportText = buildString {
                            appendLine("========================================")
                            appendLine("   ${business?.name ?: "TechPoint Electronics"} Financial Report")
                            appendLine("   Period: ${periodTabs[selectedPeriodIndex]}")
                            appendLine("========================================")
                            appendLine("Total Revenue: $currency ${String.format(Locale.US, "%,.2f", totalRevenue)}")
                            appendLine("Total Expenses: $currency ${String.format(Locale.US, "%,.2f", totalExpense)}")
                            appendLine("Net Profit: $currency ${String.format(Locale.US, "%,.2f", netProfit)}")
                            appendLine("Net Profit Margin: ${String.format(Locale.US, "%.1f", profitMargin)}%")
                            appendLine("----------------------------------------")
                            appendLine("Current Stock Valuation: $currency ${String.format(Locale.US, "%,.2f", inventoryValuation)}")
                            appendLine("Total Outstanding Credit/Debt: $currency ${String.format(Locale.US, "%,.2f", totalOutstandingDebt)}")
                            appendLine("Transactions Recorded: ${periodSales.size}")
                            appendLine("========================================")
                            appendLine("Generated by Bizly Commercial SaaS")
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export Report via"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("report_export_btn")
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                // Period Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedPeriodIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    periodTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedPeriodIndex == index,
                            onClick = { selectedPeriodIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedPeriodIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Profit & Loss Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Profit & Loss Statement (${periodTabs[selectedPeriodIndex]})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BizlyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ReportStatLine("Total Gross Sales", "$currency ${String.format(Locale.US, "%,.2f", totalRevenue)}", BizlyPrimary)
                        ReportStatLine("Operating Expenses", "-$currency ${String.format(Locale.US, "%,.2f", totalExpense)}", BizlyDanger)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ReportStatLine(
                            label = "Net Business Profit",
                            value = "$currency ${String.format(Locale.US, "%,.2f", netProfit)}",
                            color = if (netProfit >= 0) BizlySuccess else BizlyDanger,
                            isBold = true
                        )
                        ReportStatLine(
                            label = "Net Margin",
                            value = "${String.format(Locale.US, "%.1f", profitMargin)}%",
                            color = BizlyPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 7-Day Performance Chart
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily Revenue Trajectory",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BizlyTextPrimary
                        )
                        Text(
                            text = "Last 7 Days (KES)",
                            style = MaterialTheme.typography.bodySmall,
                            color = BizlyTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BizlyBarChart(
                            labels = last7DaysMetrics.map { it.dayLabel },
                            values = last7DaysMetrics.map { it.salesAmount },
                            barColor = BizlySuccess,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Inventory Valuation & Working Capital
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Inventory Valuation & Balance Sheet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BizlyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ReportStatLine("Stock Cost Valuation (At Cost)", "$currency ${String.format(Locale.US, "%,.0f", inventoryValuation)}", BizlyTextPrimary)
                        ReportStatLine("Potential Retail Valuation", "$currency ${String.format(Locale.US, "%,.0f", potentialRevenue)}", BizlySuccess)
                        ReportStatLine("Unrealized Stock Gross Profit", "$currency ${String.format(Locale.US, "%,.0f", expectedGrossProfit)}", BizlyPrimary)
                        ReportStatLine("Customer Receivables (Debts Owed)", "$currency ${String.format(Locale.US, "%,.0f", totalOutstandingDebt)}", BizlyWarning)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Employee Sales Breakdown
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sales Performance by Staff",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BizlyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val staffSales = sales.groupBy { it.employeeName }
                        staffSales.forEach { (empName, staffSaleList) ->
                            val total = staffSaleList.sumOf { it.totalAmount }
                            ReportStatLine(
                                label = "$empName (${staffSaleList.size} sales)",
                                value = "$currency ${String.format(Locale.US, "%,.0f", total)}",
                                color = BizlyPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ReportStatLine(
    label: String,
    value: String,
    color: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall,
            color = BizlyTextSecondary
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}
