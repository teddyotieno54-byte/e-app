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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Customer
import com.example.data.model.InvoiceItem
import com.example.data.model.Product
import com.example.ui.theme.BizlyBorder
import com.example.ui.theme.BizlyDanger
import com.example.ui.theme.BizlyNavy
import com.example.ui.theme.BizlyNavyDark
import com.example.ui.theme.BizlyPrimary
import com.example.ui.theme.BizlyPrimaryLight
import com.example.ui.theme.BizlySuccess
import com.example.ui.theme.BizlyTextMuted
import com.example.ui.theme.BizlyTextPrimary
import com.example.ui.theme.BizlyTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BizlyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(viewModel: BizlyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val business by viewModel.business.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val unreadNotifs = notifications.count { !it.isRead }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state for Quick Action triggers from Dashboard
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    if (!isLoggedIn || currentScreen == AppScreen.LANDING || currentScreen == AppScreen.LOGIN || currentScreen == AppScreen.SIGNUP) {
        LandingAndAuthScreen(viewModel = viewModel)
        return
    }

    if (currentScreen == AppScreen.ONBOARDING) {
        OnboardingScreen(viewModel = viewModel)
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Workspace Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BizlyNavyDark)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BizlySuccess),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Bizly SaaS", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                                Text("Workspace v1.0", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(business?.name ?: "TechPoint Electronics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text(business?.category ?: "Electronics & Gadgets", fontSize = 12.sp, color = BizlySuccess)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val menuItems = listOf(
                    DrawerMenuItem("Dashboard", Icons.Default.Dashboard, AppScreen.DASHBOARD),
                    DrawerMenuItem("Sales POS", Icons.Default.PointOfSale, AppScreen.SALES),
                    DrawerMenuItem("Inventory & Stock", Icons.Default.Inventory2, AppScreen.INVENTORY),
                    DrawerMenuItem("Customers", Icons.Default.People, AppScreen.CUSTOMERS),
                    DrawerMenuItem("Debts & Credit", Icons.Default.CreditCard, AppScreen.DEBTS),
                    DrawerMenuItem("Expenses", Icons.Default.Paid, AppScreen.EXPENSES),
                    DrawerMenuItem("Invoices", Icons.Default.Receipt, AppScreen.INVOICES),
                    DrawerMenuItem("Team & Roles", Icons.Default.Groups, AppScreen.EMPLOYEES),
                    DrawerMenuItem("Reports & P&L", Icons.Default.Assessment, AppScreen.REPORTS),
                    DrawerMenuItem("Settings & Plan", Icons.Default.Settings, AppScreen.SETTINGS),
                    DrawerMenuItem("Platform Admin", Icons.Default.AdminPanelSettings, AppScreen.ADMIN_PANEL)
                )

                menuItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label, tint = if (isSelected) BizlyPrimary else BizlyTextSecondary) },
                        label = { Text(item.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = {
                            viewModel.navigateTo(item.screen)
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = BizlyPrimary.copy(alpha = 0.1f),
                            selectedTextColor = BizlyPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = BizlyBorder)

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, null, tint = BizlyDanger) },
                    label = { Text("Sign Out", color = BizlyDanger) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Bizly",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BizlySuccess)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = business?.currency ?: "KES",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        // Quick POS button
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.SALES) },
                            colors = ButtonDefaults.buttonColors(containerColor = BizlySuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("topbar_pos_button")
                        ) {
                            Icon(Icons.Default.PointOfSale, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("POS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { viewModel.navigateTo(AppScreen.GLOBAL_SEARCH) }) {
                            Icon(Icons.Default.Search, "Search", tint = Color.White)
                        }

                        IconButton(onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) }) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifs > 0) {
                                        Badge(containerColor = BizlyDanger) { Text("$unreadNotifs") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, "Notifications", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BizlyNavyDark,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                // Mobile-First Responsive Bottom Navigation (Requirement 4)
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                        label = { Text("Dashboard", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SALES,
                        onClick = { viewModel.navigateTo(AppScreen.SALES) },
                        icon = { Icon(Icons.Default.PointOfSale, "POS") },
                        label = { Text("Sales", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.INVENTORY,
                        onClick = { viewModel.navigateTo(AppScreen.INVENTORY) },
                        icon = { Icon(Icons.Default.Inventory2, "Stock") },
                        label = { Text("Stock", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DEBTS,
                        onClick = { viewModel.navigateTo(AppScreen.DEBTS) },
                        icon = { Icon(Icons.Default.CreditCard, "Debts") },
                        label = { Text("Debts", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = { Icon(Icons.Default.Settings, "Settings") },
                        label = { Text("More", fontSize = 10.sp) }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenAddProductDialog = { showAddProductDialog = true },
                        onOpenAddCustomerDialog = { showAddCustomerDialog = true },
                        onOpenAddExpenseDialog = { showAddExpenseDialog = true },
                        onOpenCreateInvoiceDialog = { showCreateInvoiceDialog = true }
                    )
                    AppScreen.SALES -> SalesScreen(viewModel = viewModel)
                    AppScreen.INVENTORY -> InventoryScreen(viewModel = viewModel)
                    AppScreen.CUSTOMERS -> CustomersScreen(viewModel = viewModel)
                    AppScreen.DEBTS -> DebtCreditScreen(viewModel = viewModel)
                    AppScreen.EXPENSES -> ExpensesScreen(viewModel = viewModel)
                    AppScreen.INVOICES -> InvoicesScreen(viewModel = viewModel)
                    AppScreen.EMPLOYEES -> EmployeesScreen(viewModel = viewModel)
                    AppScreen.REPORTS -> ReportsScreen(viewModel = viewModel)
                    AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.GLOBAL_SEARCH -> GlobalSearchScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.ADMIN_PANEL -> AdminPanelScreen(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                    else -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenAddProductDialog = { showAddProductDialog = true },
                        onOpenAddCustomerDialog = { showAddCustomerDialog = true },
                        onOpenAddExpenseDialog = { showAddExpenseDialog = true },
                        onOpenCreateInvoiceDialog = { showCreateInvoiceDialog = true }
                    )
                }
            }
        }
    }

    // Modal dialogs triggered from Dashboard quick action buttons
    if (showAddProductDialog) {
        ProductFormDialog(
            title = "Add Product",
            initialProduct = null,
            onDismiss = { showAddProductDialog = false },
            onSave = { name, sku, category, buying, selling, stock, minStock, supplier, desc ->
                viewModel.addProduct(name, sku, category, buying, selling, stock, minStock, supplier, desc)
                showAddProductDialog = false
            }
        )
    }

    if (showAddCustomerDialog) {
        CustomerFormDialog(
            title = "Add Customer",
            initialCustomer = null,
            onDismiss = { showAddCustomerDialog = false },
            onSave = { name, phone, email, address, type, notes ->
                viewModel.addCustomer(name, phone, email, address, type, notes)
                showAddCustomerDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            categories = listOf("Rent", "Internet", "Electricity", "Salaries", "Stock", "Transport", "Marketing", "Maintenance", "Other"),
            onDismiss = { showAddExpenseDialog = false },
            onSave = { title, cat, amount, method, desc ->
                viewModel.addExpense(title, cat, amount, method, desc)
                showAddExpenseDialog = false
            }
        )
    }

    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            customers = customers,
            onDismiss = { showCreateInvoiceDialog = false },
            onCreate = { custId, name, email, phone, items, notes ->
                viewModel.createInvoice(custId, name, email, phone, items, notes)
                showCreateInvoiceDialog = false
            }
        )
    }
}

private data class DrawerMenuItem(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreen
)
