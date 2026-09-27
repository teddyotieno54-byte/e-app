package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Employee
import com.example.data.model.Product
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlySuccessLight
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel

@Composable
fun OnboardingScreen(viewModel: BizlyViewModel) {
    var step by remember { mutableIntStateOf(1) }

    // Step 1: Business Name
    var businessName by remember { mutableStateOf("TechPoint Electronics") }

    // Step 2: Category
    var selectedCategory by remember { mutableStateOf("Electronics & Gadgets") }

    // Step 3: Currency
    var selectedCurrency by remember { mutableStateOf("KES — Kenyan Shilling") }

    // Step 4: Location
    var location by remember { mutableStateOf("Moi Avenue, Digital Mall Shop G14, Nairobi") }

    // Step 5: First Product
    var firstProdName by remember { mutableStateOf("Samsung Galaxy A15 128GB") }
    var firstProdPrice by remember { mutableStateOf("21500") }
    var firstProdStock by remember { mutableStateOf("10") }

    // Step 6: Employee
    var employeeName by remember { mutableStateOf("Sharon Achieng") }
    var employeeRole by remember { mutableStateOf("Manager") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Step Progress Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BizlyNavyDark)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Business Setup Wizard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Step $step of 6",
                        style = MaterialTheme.typography.labelMedium,
                        color = BizlySuccess
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..6) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i <= step) BizlySuccess else Color(0xFF334155))
                        )
                    }
                }
            }
        }

        // Animated Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_steps"
            ) { targetStep ->
                when (targetStep) {
                    1 -> StepCard(
                        title = "What is your business name?",
                        subtitle = "This will appear on all your receipts, invoices, and reports.",
                        icon = Icons.Default.Business
                    ) {
                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text("Business Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ob_biz_name_input"),
                            singleLine = true
                        )
                    }

                    2 -> StepCard(
                        title = "Choose your business category",
                        subtitle = "Bizly will tailor product fields and tax settings to your niche.",
                        icon = Icons.Default.Category
                    ) {
                        val categories = listOf(
                            "Electronics & Gadgets",
                            "Retail Shop / Mini-Market",
                            "Salon / Barbershop",
                            "Fashion & Clothing Boutique",
                            "Restaurant / Café / Bar",
                            "Hardware & Electrical",
                            "Wholesale & Distribution",
                            "Services & Repairs"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) BizlyPrimary.copy(alpha = 0.1f) else Color.White)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) BizlyPrimary else BizlyBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedCategory = cat }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, if (isSelected) BizlyPrimary else BizlyBorder, CircleShape)
                                            .background(if (isSelected) BizlyPrimary else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = BizlyTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    3 -> StepCard(
                        title = "Select your default currency",
                        subtitle = "Default for Kenya is KES (Kenyan Shilling).",
                        icon = Icons.Default.Paid
                    ) {
                        val currencies = listOf(
                            "KES — Kenyan Shilling",
                            "USD — US Dollar ($)",
                            "TZS — Tanzanian Shilling",
                            "UGX — Ugandan Shilling"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currencies.forEach { cur ->
                                val isSelected = selectedCurrency.startsWith(cur.substring(0, 3))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) BizlySuccessLight else Color.White)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) BizlySuccess else BizlyBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedCurrency = cur }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = cur,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) BizlySuccess else BizlyTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    4 -> StepCard(
                        title = "Where is your business located?",
                        subtitle = "Help customers and suppliers find your physical shop or office.",
                        icon = Icons.Default.LocationOn
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Physical Location / City / Mall") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    5 -> StepCard(
                        title = "Add your first product or service",
                        subtitle = "You can add hundreds more from the inventory manager anytime.",
                        icon = Icons.Default.ShoppingCart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = firstProdName,
                                onValueChange = { firstProdName = it },
                                label = { Text("Product / Service Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = firstProdPrice,
                                onValueChange = { firstProdPrice = it },
                                label = { Text("Selling Price (KES)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = firstProdStock,
                                onValueChange = { firstProdStock = it },
                                label = { Text("Initial Stock Quantity") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    6 -> StepCard(
                        title = "Invite an employee or manager",
                        subtitle = "Delegate sales recording and inventory without sharing full owner permissions.",
                        icon = Icons.Default.GroupAdd
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = employeeName,
                                onValueChange = { employeeName = it },
                                label = { Text("Employee Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = employeeRole,
                                onValueChange = { employeeRole = it },
                                label = { Text("Assigned Role (Manager / Salesperson)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BizlySuccessLight)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Your business is ready! Click finish to launch your customized workspace dashboard.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BizlySuccess,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (step < 6) {
                        step++
                    } else {
                        val firstProduct = if (firstProdName.isNotBlank()) {
                            Product(
                                name = firstProdName,
                                sku = "SKU-001",
                                category = selectedCategory,
                                buyingPrice = (firstProdPrice.toDoubleOrNull() ?: 0.0) * 0.8,
                                sellingPrice = firstProdPrice.toDoubleOrNull() ?: 0.0,
                                currentStock = firstProdStock.toIntOrNull() ?: 5,
                                minStockLevel = 3,
                                supplier = "Initial Stock",
                                description = "Added during business onboarding"
                            )
                        } else null

                        val employees = if (employeeName.isNotBlank()) {
                            listOf(
                                Employee(name = employeeName, email = "staff@bizly.app", phone = "+254 700 000000", role = employeeRole)
                            )
                        } else emptyList()

                        viewModel.completeOnboarding(
                            bizName = businessName,
                            category = selectedCategory,
                            currency = selectedCurrency.substring(0, 3),
                            location = location,
                            firstProduct = firstProduct,
                            employees = employees
                        )
                    }
                },
                modifier = Modifier.testTag("onboarding_next_finish_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (step == 6) BizlySuccess else BizlyPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (step == 6) "Launch Dashboard" else "Next Step",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StepCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BizlyBorder)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BizlyPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BizlyPrimary, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BizlyTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = BizlyTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            content()
        }
    }
}
