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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BizlyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val business by viewModel.business.collectAsStateWithLifecycle()

    var name by remember(business) { mutableStateOf(business?.name ?: "TechPoint Electronics") }
    var category by remember(business) { mutableStateOf(business?.category ?: "Electronics & Gadgets") }
    var location by remember(business) { mutableStateOf(business?.location ?: "Moi Avenue, Nairobi") }
    var phone by remember(business) { mutableStateOf(business?.phone ?: "+254 712 345678") }
    var email by remember(business) { mutableStateOf(business?.email ?: "sales@techpoint-ke.com") }
    var currency by remember(business) { mutableStateOf(business?.currency ?: "KES") }

    var mpesaTill by remember(business) { mutableStateOf(business?.mpesaTillOrPaybill ?: "782910") }
    var mpesaType by remember(business) { mutableStateOf(business?.mpesaType ?: "Till (Buy Goods)") }

    var showResetConfirm by remember { mutableStateOf(false) }

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
                        text = "Business Settings & SaaS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Workspace Profile & M-PESA Config",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = {
                        viewModel.updateBusinessProfile(name, category, currency, location, phone, email)
                        viewModel.updateMpesaSettings(mpesaTill, mpesaType)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("settings_save_btn")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1. Business Profile Card
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSectionHeader("Business Profile", Icons.Default.Business)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Business / Trade Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Business Category") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Physical Location") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Official Phone") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Official Email") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Financial & Currency Settings
            item {
                SettingsSectionHeader("Financial & Tax Configurations", Icons.Default.Paid)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Active Currency", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("KES", "USD", "TZS", "UGX").forEach { cur ->
                                FilterChip(
                                    selected = currency == cur,
                                    onClick = { currency = cur },
                                    label = { Text(cur, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                                )
                            }
                        }
                        Text("Standard Tax (VAT in Kenya: 16%) is calculated automatically at checkout.", fontSize = 11.sp, color = BizlyTextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. M-PESA Integration Layer (Requirement 9)
            item {
                SettingsSectionHeader("M-PESA Payment Gateway", Icons.Default.PhoneAndroid)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BizlySuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Safaricom Daraja API Bridge: CONNECTED", fontWeight = FontWeight.Bold, color = BizlySuccess, fontSize = 12.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = mpesaTill,
                                onValueChange = { mpesaTill = it },
                                label = { Text("Till / Paybill Number") },
                                modifier = Modifier.weight(1.3f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = mpesaType,
                                onValueChange = { mpesaType = it },
                                label = { Text("Account Type") },
                                modifier = Modifier.weight(1.3f),
                                singleLine = true
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Note: API credentials and consumer secret tokens are managed server-side and never exposed in client code. Live STK Push simulation is active.",
                                fontSize = 11.sp,
                                color = BizlyTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Subscription & Plan (Requirement 20)
            item {
                SettingsSectionHeader("Subscription & SaaS Plan", Icons.Default.CreditCard)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Current Plan", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                                Text(business?.subscriptionPlan ?: "Business", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = BizlyPrimary)
                            }
                            StatusBadge(status = "Active")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Upgrade or switch plan tier:", style = MaterialTheme.typography.labelSmall, color = BizlyTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Free", "Starter", "Business", "Enterprise").forEach { plan ->
                                FilterChip(
                                    selected = (business?.subscriptionPlan ?: "Business") == plan,
                                    onClick = { viewModel.updateSubscriptionPlan(plan) },
                                    label = { Text(plan, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Platform Admin & Demo Workspace Controls
            item {
                SettingsSectionHeader("Platform Management & Demo Data", Icons.Default.AdminPanelSettings)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onNavigate(AppScreen.ADMIN_PANEL) },
                            colors = ButtonDefaults.buttonColors(containerColor = BizlyNavyDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_admin_panel_btn")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open SaaS Platform Administrator Panel")
                        }

                        OutlinedButton(
                            onClick = { showResetConfirm = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp), tint = BizlyPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reload TechPoint Electronics Demo Dataset", color = BizlyPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp), tint = BizlyDanger)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out to Landing Page", color = BizlyDanger)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reload Demo Data?") },
            text = { Text("This will reset all products, customers, transactions, and expenses to the standard TechPoint Electronics demo dataset.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.loadDemoData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BizlyPrimary)
                ) {
                    Text("Reload Demo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BizlyPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = BizlyTextPrimary)
    }
}
