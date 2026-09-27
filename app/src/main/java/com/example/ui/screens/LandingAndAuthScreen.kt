package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlyPrimaryLight
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlySuccessLight
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LandingAndAuthScreen(viewModel: BizlyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Landing, 1: Login, 2: Sign Up

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Navigation Header
        SurfaceHeader(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it },
            onExploreDemo = { viewModel.loadDemoData() }
        )

        when (selectedTab) {
            0 -> LandingPageContent(
                onStartFree = { selectedTab = 2 },
                onLogin = { selectedTab = 1 },
                onExploreDemo = { viewModel.loadDemoData() }
            )
            1 -> LoginForm(
                viewModel = viewModel,
                onSwitchToSignUp = { selectedTab = 2 },
                onExploreDemo = { viewModel.loadDemoData() }
            )
            2 -> SignUpForm(
                viewModel = viewModel,
                onSwitchToLogin = { selectedTab = 1 },
                onProceedToOnboarding = { viewModel.navigateTo(AppScreen.ONBOARDING) }
            )
        }
    }
}

@Composable
private fun SurfaceHeader(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onExploreDemo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BizlyNavyDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onSelectTab(0) }
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BizlySuccess),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Bizly",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Run smarter",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selectedTab != 1) {
                TextButton(
                    onClick = { onSelectTab(1) },
                    modifier = Modifier.testTag("nav_login_button")
                ) {
                    Text("Sign In", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = onExploreDemo,
                colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("nav_demo_button")
            ) {
                Text("View Demo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun LandingPageContent(
    onStartFree: () -> Unit,
    onLogin: () -> Unit,
    onExploreDemo: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BizlyNavyDark)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(BizlySuccess.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "BUILT FOR BUSINESSES IN KENYA & AFRICA",
                            color = BizlySuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Run your business smarter.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sales, inventory, customers, M-PESA payments, credit ledger, and business insights — all in one simple platform.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartFree,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_start_free_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BizlyPrimaryLight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Start Free", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onExploreDemo,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_view_demo_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Explore Demo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Trust badge
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BizlySuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Trusted by retail shops, mini-marts, electronics hubs, and salons in Nairobi, Mombasa, Kisumu & Eldoret.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BizlyTextSecondary
                    )
                }
            }
        }

        // Feature Highlights Grid
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Everything you need to replace messy notebooks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val features = listOf(
                FeatureItem("Fast Sales POS", "Record cash, M-PESA & credit sales with automatic stock reduction.", Icons.Default.PointOfSale, BizlyPrimary),
                FeatureItem("M-PESA Integration", "Record Till & Paybill confirmation codes with instant transaction matching.", Icons.Default.PhoneAndroid, BizlySuccess),
                FeatureItem("Inventory & Alerts", "Track stock levels, buying vs selling margins, and out-of-stock alerts.", Icons.Default.Inventory2, Color(0xFF8B5CF6)),
                FeatureItem("Debt & Credit Tracker", "Keep track of customer balances with automated SMS/WhatsApp reminders.", Icons.Default.CreditCard, Color(0xFFEA580C)),
                FeatureItem("Invoices & Receipts", "Generate digital receipts and professional invoices in seconds.", Icons.Default.ReceiptLong, Color(0xFF0284C7)),
                FeatureItem("Profit & Insights", "Instant daily, weekly, and monthly net profit and revenue reports.", Icons.Default.TrendingUp, BizlySuccess)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.forEach { feat ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(feat.color.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = feat.icon,
                                    contentDescription = null,
                                    tint = feat.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = feat.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BizlyTextPrimary
                                )
                                Text(
                                    text = feat.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BizlyTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pricing Preview
        item {
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "Simple, transparent pricing",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PricingCard("Free", "KES 0", "Forever free", listOf("Up to 30 products", "Basic sales & cash recording", "Single user account"), false, onStartFree)
                PricingCard("Starter", "KES 1,500", "per month", listOf("Unlimited products", "M-PESA manual tracking", "Digital receipts & debt manager", "2 employee logins"), false, onStartFree)
                PricingCard("Business (Popular)", "KES 3,500", "per month", listOf("Everything in Starter", "Automated STK push integration", "Inventory audit history & P&L reports", "5 team accounts & role permissions"), true, onStartFree)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private data class FeatureItem(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun PricingCard(
    plan: String,
    price: String,
    period: String,
    features: List<String>,
    isHighlighted: Boolean,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) BizlyNavyDark else Color.White
        ),
        border = if (isHighlighted) null else CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isHighlighted) Color.White else BizlyTextPrimary
                )
                if (isHighlighted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BizlySuccess)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("RECOMMENDED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = if (isHighlighted) Color.White else BizlyPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHighlighted) Color(0xFF94A3B8) else BizlyTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            features.forEach { feat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = BizlySuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = feat,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isHighlighted) Color(0xFFCBD5E1) else BizlyTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHighlighted) BizlySuccess else BizlyPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select $plan Plan", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun LoginForm(
    viewModel: BizlyViewModel,
    onSwitchToSignUp: () -> Unit,
    onExploreDemo: () -> Unit
) {
    var email by remember { mutableStateOf("kelvin@techpoint-ke.com") }
    var password by remember { mutableStateOf("••••••••") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Welcome back to Bizly",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Text(
                text = "Sign in to manage TechPoint Electronics workspace",
                style = MaterialTheme.typography.bodySmall,
                color = BizlyTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email or Phone") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.login(email, "Owner") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BizlyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sign In to Workspace", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onExploreDemo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_demo_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("1-Tap Demo Login (TechPoint)", color = BizlySuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = BizlyTextSecondary, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onSwitchToSignUp) {
                    Text("Register Free", color = BizlyPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpForm(
    viewModel: BizlyViewModel,
    onSwitchToLogin: () -> Unit,
    onProceedToOnboarding: () -> Unit
) {
    var fullName by remember { mutableStateOf("Kelvin Mworia") }
    var businessName by remember { mutableStateOf("TechPoint Electronics") }
    var email by remember { mutableStateOf("kelvin@techpoint-ke.com") }
    var phone by remember { mutableStateOf("+254 712 345678") }
    var password by remember { mutableStateOf("Secret123") }
    var businessType by remember { mutableStateOf("Electronics & Gadgets") }

    val businessTypes = listOf(
        "Electronics & Gadgets",
        "Retail Shop / Mini-Market",
        "Salon / Barbershop",
        "Fashion & Clothing",
        "Restaurant / Café",
        "Hardware & Construction",
        "Pharmacy / Chemist",
        "Wholesale & Distribution",
        "Professional Services"
    )
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Create your Bizly Account",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Text(
                text = "Set up your workspace in under 2 minutes",
                style = MaterialTheme.typography.bodySmall,
                color = BizlyTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number (M-PESA ready)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = businessType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Business Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            businessTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        businessType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.login(email, "Owner")
                            onProceedToOnboarding()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("signup_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Create Workspace & Continue", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already registered?", color = BizlyTextSecondary, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onSwitchToLogin) {
                    Text("Sign In", color = BizlyPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
