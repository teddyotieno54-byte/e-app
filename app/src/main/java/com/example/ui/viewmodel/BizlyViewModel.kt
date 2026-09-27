package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Business
import com.example.data.model.Customer
import com.example.data.model.Debt
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.NotificationItem
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StockAdjustment
import com.example.data.repository.BizlyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class AppScreen {
    LANDING,
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    ONBOARDING,
    DASHBOARD,
    SALES,
    INVENTORY,
    CUSTOMERS,
    DEBTS,
    EXPENSES,
    INVOICES,
    EMPLOYEES,
    REPORTS,
    NOTIFICATIONS,
    SETTINGS,
    GLOBAL_SEARCH,
    ADMIN_PANEL
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val lineTotal: Double get() = product.sellingPrice * quantity
}

data class DaySalesMetric(
    val dayLabel: String,
    val dateString: String,
    val salesAmount: Double,
    val expenseAmount: Double,
    val profitAmount: Double
)

class BizlyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BizlyRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BizlyRepository(db.bizlyDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    // --- Navigation & Auth State ---
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow("Kelvin Mworia (Owner)")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun login(email: String, role: String = "Owner") {
        _isLoggedIn.value = true
        _currentUser.value = if (email.isNotBlank()) email.substringBefore("@").replaceFirstChar { it.uppercase() } + " ($role)" else "Business Owner"
        _currentScreen.value = AppScreen.DASHBOARD
        showToast("Welcome back to Bizly!")
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = AppScreen.LANDING
        showToast("Logged out successfully.")
    }

    // --- Core Data Flows ---
    val business: StateFlow<Business?> = repository.businessFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val products: StateFlow<List<Product>> = repository.productsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Customer>> = repository.customersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<Sale>> = repository.salesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSaleItems: StateFlow<List<SaleItem>> = repository.allSaleItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.expensesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<Debt>> = repository.debtsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<Invoice>> = repository.invoicesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val employees: StateFlow<List<Employee>> = repository.employeesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockAdjustments: StateFlow<List<StockAdjustment>> = repository.stockAdjustmentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- POS Cart State ---
    private val _cart = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val cart: StateFlow<Map<Long, Int>> = _cart.asStateFlow()

    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

    private val _paymentMethod = MutableStateFlow("M-PESA")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _isCreditSale = MutableStateFlow(false)
    val isCreditSale: StateFlow<Boolean> = _isCreditSale.asStateFlow()

    private val _taxIncluded = MutableStateFlow(true)
    val taxIncluded: StateFlow<Boolean> = _taxIncluded.asStateFlow()

    private val _discountPercent = MutableStateFlow(0)
    val discountPercent: StateFlow<Int> = _discountPercent.asStateFlow()

    private val _mpesaPhoneNumber = MutableStateFlow("254712345678")
    val mpesaPhoneNumber: StateFlow<String> = _mpesaPhoneNumber.asStateFlow()

    private val _mpesaCodeInput = MutableStateFlow("")
    val mpesaCodeInput: StateFlow<String> = _mpesaCodeInput.asStateFlow()

    private val _isStkPushing = MutableStateFlow(false)
    val isStkPushing: StateFlow<Boolean> = _isStkPushing.asStateFlow()

    private val _completedSaleReceipt = MutableStateFlow<Pair<Sale, List<SaleItem>>?>(null)
    val completedSaleReceipt: StateFlow<Pair<Sale, List<SaleItem>>?> = _completedSaleReceipt.asStateFlow()

    fun addToCart(product: Product) {
        val current = _cart.value.toMutableMap()
        val currentQty = current[product.id] ?: 0
        current[product.id] = currentQty + 1
        _cart.value = current
    }

    fun removeFromCart(productId: Long) {
        val current = _cart.value.toMutableMap()
        val currentQty = current[productId] ?: 0
        if (currentQty > 1) {
            current[productId] = currentQty - 1
        } else {
            current.remove(productId)
        }
        _cart.value = current
    }

    fun removeLineItem(productId: Long) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
        _selectedCustomerId.value = null
        _discountPercent.value = 0
        _isCreditSale.value = false
        _mpesaCodeInput.value = ""
    }

    fun setSelectedCustomer(customerId: Long?) {
        _selectedCustomerId.value = customerId
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
        _isCreditSale.value = (method == "Credit")
    }

    fun setCreditSale(isCredit: Boolean) {
        _isCreditSale.value = isCredit
        if (isCredit) _paymentMethod.value = "Credit"
    }

    fun setTaxIncluded(enabled: Boolean) {
        _taxIncluded.value = enabled
    }

    fun setDiscountPercent(percent: Int) {
        _discountPercent.value = percent
    }

    fun setMpesaPhoneNumber(phone: String) {
        _mpesaPhoneNumber.value = phone
    }

    fun setMpesaCodeInput(code: String) {
        _mpesaCodeInput.value = code
    }

    fun dismissReceipt() {
        _completedSaleReceipt.value = null
    }

    // Trigger realistic M-PESA STK Push Simulation
    fun triggerMpesaStkPush(amount: Double) {
        viewModelScope.launch {
            _isStkPushing.value = true
            delay(2200) // Realistic Safaricom STK prompt delay
            val randomSuffix = (1000..9999).random()
            val letters = ('A'..'Z').toList()
            val prefix = "${letters.random()}${letters.random()}${(10..99).random()}${letters.random()}"
            val generatedCode = "QK$randomSuffix$prefix"
            _mpesaCodeInput.value = generatedCode
            _isStkPushing.value = false
            showToast("M-PESA Confirmed! Code: $generatedCode for KES ${String.format(Locale.US, "%,.2f", amount)}")
        }
    }

    fun checkout(productsList: List<Product>, customersList: List<Customer>) {
        val cartMap = _cart.value
        if (cartMap.isEmpty()) {
            showToast("Cart is empty! Add products first.")
            return
        }

        viewModelScope.launch {
            val items = cartMap.mapNotNull { (prodId, qty) ->
                val prod = productsList.find { it.id == prodId }
                if (prod != null) {
                    SaleItem(
                        saleId = 0,
                        productId = prod.id,
                        productName = prod.name,
                        quantity = qty,
                        unitPrice = prod.sellingPrice,
                        totalPrice = prod.sellingPrice * qty
                    )
                } else null
            }

            val subtotal = items.sumOf { it.totalPrice }
            val discountAmt = subtotal * (_discountPercent.value / 100.0)
            val taxableBase = subtotal - discountAmt
            val taxAmt = if (_taxIncluded.value) (taxableBase * 0.16) / 1.16 else 0.0
            val total = taxableBase

            val customerId = _selectedCustomerId.value
            val customer = customersList.find { it.id == customerId }
            val customerName = customer?.name ?: "Walk-in Customer"

            val saleNumber = "TPE-${SimpleDateFormat("yyyy", Locale.US).format(Date())}-${Random.nextInt(1000, 9999)}"
            val paymentMethod = _paymentMethod.value
            val isCredit = _isCreditSale.value || paymentMethod == "Credit"

            val mpesaCode = if (paymentMethod == "M-PESA") {
                if (_mpesaCodeInput.value.isNotBlank()) _mpesaCodeInput.value
                else "QK${Random.nextInt(100000, 999999)}KE"
            } else ""

            val sale = Sale(
                saleNumber = saleNumber,
                customerId = customerId,
                customerName = customerName,
                subtotal = subtotal,
                discountAmount = discountAmt,
                taxAmount = taxAmt,
                totalAmount = total,
                paymentMethod = paymentMethod,
                paymentStatus = if (isCredit) "Credit" else "Paid",
                mpesaCode = mpesaCode,
                employeeName = _currentUser.value
            )

            val saleId = repository.completeSale(sale, items, isCredit)
            val finalSale = sale.copy(id = saleId)
            val finalItems = items.map { it.copy(saleId = saleId) }

            _completedSaleReceipt.value = Pair(finalSale, finalItems)
            clearCart()
            showToast("Sale $saleNumber completed successfully!")
        }
    }

    // --- Product Actions ---
    fun addProduct(
        name: String,
        sku: String,
        category: String,
        buyingPrice: Double,
        sellingPrice: Double,
        stock: Int,
        minStock: Int,
        supplier: String,
        description: String
    ) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                sku = if (sku.isBlank()) "SKU-${Random.nextInt(1000, 9999)}" else sku,
                category = category,
                buyingPrice = buyingPrice,
                sellingPrice = sellingPrice,
                currentStock = stock,
                minStockLevel = minStock,
                supplier = supplier,
                description = description,
                status = when {
                    stock <= 0 -> "Out of Stock"
                    stock <= minStock -> "Low Stock"
                    else -> "In Stock"
                }
            )
            repository.addProduct(product)
            showToast("Product $name added successfully!")
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            showToast("Product ${product.name} updated.")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showToast("Product ${product.name} removed.")
        }
    }

    fun adjustStock(productId: Long, type: String, quantityChange: Int, reason: String) {
        viewModelScope.launch {
            repository.adjustStock(productId, type, quantityChange, reason)
            showToast("Stock adjusted ($type: $quantityChange units).")
        }
    }

    // --- Customer Actions ---
    fun addCustomer(
        name: String,
        phone: String,
        email: String,
        address: String,
        customerType: String,
        notes: String
    ) {
        viewModelScope.launch {
            val customer = Customer(
                name = name,
                phone = phone,
                email = email,
                address = address,
                customerType = customerType,
                notes = notes
            )
            repository.addCustomer(customer)
            showToast("Customer $name saved.")
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            showToast("Customer ${customer.name} updated.")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            showToast("Customer deleted.")
        }
    }

    // --- Expense Actions ---
    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        paymentMethod: String,
        description: String
    ) {
        viewModelScope.launch {
            val expense = Expense(
                title = title,
                category = category,
                amount = amount,
                paymentMethod = paymentMethod,
                description = description
            )
            repository.addExpense(expense)
            showToast("Expense KES $amount recorded under $category.")
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            showToast("Expense removed.")
        }
    }

    // --- Debt Actions ---
    fun recordDebtPayment(debtId: Long, amount: Double, method: String, reference: String) {
        viewModelScope.launch {
            repository.recordDebtPayment(debtId, amount, method, reference)
            showToast("Repayment of KES $amount recorded via $method.")
        }
    }

    // --- Invoice Actions ---
    fun createInvoice(
        customerId: Long,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        items: List<InvoiceItem>,
        notes: String
    ) {
        viewModelScope.launch {
            val subtotal = items.sumOf { it.amount }
            val taxAmount = subtotal * 0.16
            val total = subtotal + taxAmount
            val invNumber = "INV-${SimpleDateFormat("yyyy", Locale.US).format(Date())}-${Random.nextInt(100, 999)}"

            val invoice = Invoice(
                invoiceNumber = invNumber,
                customerId = customerId,
                customerName = customerName,
                customerEmail = customerEmail,
                customerPhone = customerPhone,
                subtotal = subtotal,
                taxAmount = taxAmount,
                totalAmount = total,
                notes = notes
            )
            repository.createInvoice(invoice, items)
            showToast("Invoice $invNumber created successfully!")
        }
    }

    fun updateInvoiceStatus(invoice: Invoice, newStatus: String) {
        viewModelScope.launch {
            repository.updateInvoice(invoice.copy(status = newStatus))
            showToast("Invoice marked as $newStatus.")
        }
    }

    // --- Employee Actions ---
    fun addEmployee(name: String, email: String, phone: String, role: String) {
        viewModelScope.launch {
            val employee = Employee(
                name = name,
                email = email,
                phone = phone,
                role = role
            )
            repository.addEmployee(employee)
            showToast("Employee $name ($role) added.")
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
            showToast("Employee updated.")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            showToast("Employee removed.")
        }
    }

    // --- Business Settings & Profile Actions ---
    fun updateBusinessProfile(
        name: String,
        category: String,
        currency: String,
        location: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            val current = repository.getBusiness() ?: return@launch
            repository.updateBusiness(
                current.copy(
                    name = name,
                    category = category,
                    currency = currency,
                    location = location,
                    phone = phone,
                    email = email
                )
            )
            showToast("Business profile updated.")
        }
    }

    fun updateMpesaSettings(
        tillOrPaybill: String,
        type: String
    ) {
        viewModelScope.launch {
            val current = repository.getBusiness() ?: return@launch
            repository.updateBusiness(
                current.copy(
                    mpesaTillOrPaybill = tillOrPaybill,
                    mpesaType = type
                )
            )
            showToast("M-PESA settings saved.")
        }
    }

    fun updateSubscriptionPlan(plan: String) {
        viewModelScope.launch {
            val current = repository.getBusiness() ?: return@launch
            repository.updateBusiness(current.copy(subscriptionPlan = plan))
            showToast("Subscription upgraded to $plan plan!")
        }
    }

    // --- Onboarding / Reset ---
    fun completeOnboarding(
        bizName: String,
        category: String,
        currency: String,
        location: String,
        firstProduct: Product?,
        employees: List<Employee>
    ) {
        viewModelScope.launch {
            repository.createNewWorkspace(bizName, category, currency, location, firstProduct, employees)
            _isLoggedIn.value = true
            _currentScreen.value = AppScreen.DASHBOARD
            showToast("Your business $bizName is ready!")
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.resetToDemoData()
            _isLoggedIn.value = true
            _currentScreen.value = AppScreen.DASHBOARD
            showToast("TechPoint Electronics demo data reloaded!")
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            showToast("All notifications marked as read.")
        }
    }

    // --- Analytics Computations ---
    val last7DaysMetrics: StateFlow<List<DaySalesMetric>> = combine(sales, expenses) { salesList, expensesList ->
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("d MMM", Locale.US)
        val result = mutableListOf<DaySalesMetric>()

        for (i in 6 downTo 0) {
            val targetCal = Calendar.getInstance()
            targetCal.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = targetCal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + 86400000L

            val daySales = salesList.filter { it.createdAt in dayStart until dayEnd }.sumOf { it.totalAmount }
            val dayExpenses = expensesList.filter { it.dateMillis in dayStart until dayEnd }.sumOf { it.amount }

            result.add(
                DaySalesMetric(
                    dayLabel = dayFormat.format(Date(dayStart)),
                    dateString = dateFormat.format(Date(dayStart)),
                    salesAmount = daySales,
                    expenseAmount = dayExpenses,
                    profitAmount = daySales - dayExpenses
                )
            )
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
