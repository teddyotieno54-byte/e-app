package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel

@Composable
fun AdminPanelScreen(
    viewModel: BizlyViewModel,
    onNavigateBack: () -> Unit
) {
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Bizly SaaS Platform Admin",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Global Multi-Tenant Infrastructure",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text("Platform Telemetry & Revenue", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Active Workspaces",
                        value = "142 Businesses",
                        icon = Icons.Default.Business,
                        subtitle = "+18 this month",
                        isPositiveTrend = true,
                        accentColor = BizlyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Monthly Revenue (MRR)",
                        value = "KES 486,500",
                        icon = Icons.Default.Paid,
                        subtitle = "SaaS Subscriptions",
                        isPositiveTrend = true,
                        accentColor = BizlySuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Total Active Users",
                        value = "489 Staff Accounts",
                        icon = Icons.Default.Group,
                        accentColor = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total GMV Processed",
                        value = "KES 28.4 Million",
                        icon = Icons.Default.TrendingUp,
                        subtitle = "Kenya & East Africa",
                        accentColor = BizlySuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text("Active Tenant Businesses", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                val tenants = listOf(
                    TenantInfo("TechPoint Electronics", "Nairobi, CBD", "Business Tier (KES 3,500/mo)", "KES 2.4M GMV", "Active"),
                    TenantInfo("Mombasa Auto Spare Parts", "Mombasa Old Town", "Enterprise Tier (KES 8,000/mo)", "KES 5.1M GMV", "Active"),
                    TenantInfo("Eldoret Grain & Flour Wholesalers", "Eldoret Town", "Business Tier (KES 3,500/mo)", "KES 3.8M GMV", "Active"),
                    TenantInfo("Mama Sarah Salon & Spa", "Kilimani, Nairobi", "Starter Tier (KES 1,500/mo)", "KES 420K GMV", "Active"),
                    TenantInfo("Ruiru General Hardware Store", "Ruiru Bypass", "Business Tier (KES 3,500/mo)", "KES 1.9M GMV", "Active")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tenants.forEach { tenant ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tenant.name, fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                                    Text("${tenant.location} • ${tenant.tier}", style = MaterialTheme.typography.bodySmall, color = BizlyTextSecondary)
                                    Text("Processed Volume: ${tenant.gmv}", style = MaterialTheme.typography.labelSmall, color = BizlySuccess)
                                }
                                StatusBadge(status = tenant.status)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text("System Audit Logs", fontWeight = FontWeight.Bold, color = BizlyTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                val logs = listOf(
                    "M-PESA STK Callback: TechPoint Electronics (KES 21,500) successfully settled.",
                    "Subscription Renewal: Mombasa Auto Spare Parts auto-debited KES 8,000 via Card.",
                    "New Onboarding: Mama Sarah Salon completed business setup wizard.",
                    "Database Backup: Automated hourly snapshot verified and encrypted."
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        logs.forEach { log ->
                            Text("• $log", fontSize = 11.sp, color = BizlyTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

private data class TenantInfo(
    val name: String,
    val location: String,
    val tier: String,
    val gmv: String,
    val status: String
)
