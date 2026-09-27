package com.example.data.repository

import com.example.data.dao.BizlyDao
import com.example.data.database.DemoDataSeeder
import com.example.data.model.Business
import com.example.data.model.Customer
import com.example.data.model.Debt
import com.example.data.model.DebtPayment
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.NotificationItem
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StockAdjustment
import kotlinx.coroutines.flow.Flow

class BizlyRepository(private val dao: BizlyDao) {

    val businessFlow: Flow<Business?> = dao.getBusinessFlow(1)
    val productsFlow: Flow<List<Product>> = dao.getProducts(1)
    val customersFlow: Flow<List<Customer>> = dao.getCustomers(1)
    val salesFlow: Flow<List<Sale>> = dao.getSales(1)
    val allSaleItemsFlow: Flow<List<SaleItem>> = dao.getAllSaleItems()
    val expensesFlow: Flow<List<Expense>> = dao.getExpenses(1)
    val debtsFlow: Flow<List<Debt>> = dao.getDebts(1)
    val invoicesFlow: Flow<List<Invoice>> = dao.getInvoices(1)
    val employeesFlow: Flow<List<Employee>> = dao.getEmployees(1)
    val notificationsFlow: Flow<List<NotificationItem>> = dao.getNotifications(1)
    val stockAdjustmentsFlow: Flow<List<StockAdjustment>> = dao.getStockAdjustments()

    suspend fun getBusiness(): Business? = dao.getBusiness(1)

    suspend fun updateBusiness(business: Business) = dao.updateBusiness(business)

    suspend fun ensureInitialData() {
        val current = dao.getBusiness(1)
        if (current == null) {
            DemoDataSeeder.seedDemoData(dao)
        }
    }

    suspend fun resetToDemoData() {
        DemoDataSeeder.seedDemoData(dao)
    }

    suspend fun createNewWorkspace(
        businessName: String,
        category: String,
        currency: String,
        location: String,
        firstProduct: Product?,
        invitedEmployees: List<Employee>
    ) {
        // Clear old data for a fresh custom workspace
        dao.clearProducts()
        dao.clearCustomers()
        dao.clearSales()
        dao.clearSaleItems()
        dao.clearExpenses()
        dao.clearDebts()
        dao.clearDebtPayments()
        dao.clearInvoices()
        dao.clearInvoiceItems()
        dao.clearEmployees()
        dao.clearNotifications()
        dao.clearStockAdjustments()

        val newBiz = Business(
            id = 1,
            name = businessName,
            category = category,
            currency = currency,
            location = location,
            phone = "+254 700 000000",
            email = "info@${businessName.lowercase().replace(" ", "")}.co.ke"
        )
        dao.insertBusiness(newBiz)

        firstProduct?.let { dao.insertProduct(it.copy(businessId = 1)) }
        if (invitedEmployees.isNotEmpty()) {
            dao.insertEmployees(invitedEmployees.map { it.copy(businessId = 1) })
        } else {
            dao.insertEmployee(Employee(name = "Business Owner", email = newBiz.email, phone = newBiz.phone, role = "Owner", businessId = 1))
        }

        dao.insertNotification(
            NotificationItem(
                businessId = 1,
                title = "Welcome to Bizly!",
                message = "Your workspace $businessName is now set up and ready to run.",
                type = "system",
                isRead = false
            )
        )
    }

    // --- Product & Stock Operations ---
    suspend fun addProduct(product: Product): Long {
        val id = dao.insertProduct(product)
        dao.insertStockAdjustment(
            StockAdjustment(
                productId = id,
                productName = product.name,
                type = "Initial Stock",
                quantityChange = product.currentStock,
                previousStock = 0,
                newStock = product.currentStock,
                reason = "Product created"
            )
        )
        return id
    }

    suspend fun updateProduct(product: Product) {
        val old = dao.getProductById(product.id)
        if (old != null && old.currentStock != product.currentStock) {
            val diff = product.currentStock - old.currentStock
            dao.insertStockAdjustment(
                StockAdjustment(
                    productId = product.id,
                    productName = product.name,
                    type = if (diff > 0) "Stock In" else "Stock Out",
                    quantityChange = diff,
                    previousStock = old.currentStock,
                    newStock = product.currentStock,
                    reason = "Product details edited"
                )
            )
        }
        val status = when {
            product.currentStock <= 0 -> "Out of Stock"
            product.currentStock <= product.minStockLevel -> "Low Stock"
            else -> "In Stock"
        }
        dao.updateProduct(product.copy(status = status))
    }

    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)

    suspend fun adjustStock(productId: Long, type: String, quantityChange: Int, reason: String) {
        val product = dao.getProductById(productId) ?: return
        val newStock = (product.currentStock + quantityChange).coerceAtLeast(0)
        val newStatus = when {
            newStock <= 0 -> "Out of Stock"
            newStock <= product.minStockLevel -> "Low Stock"
            else -> "In Stock"
        }
        dao.updateProductStock(productId, newStock, newStatus)
        dao.insertStockAdjustment(
            StockAdjustment(
                productId = productId,
                productName = product.name,
                type = type,
                quantityChange = quantityChange,
                previousStock = product.currentStock,
                newStock = newStock,
                reason = reason
            )
        )

        // Check stock alerts
        if (newStock <= 0) {
            dao.insertNotification(
                NotificationItem(
                    businessId = 1,
                    title = "Out of Stock Alert",
                    message = "${product.name} is now out of stock!",
                    type = "stock"
                )
            )
        } else if (newStock <= product.minStockLevel) {
            dao.insertNotification(
                NotificationItem(
                    businessId = 1,
                    title = "Low Stock Warning",
                    message = "${product.name} is down to $newStock units.",
                    type = "stock"
                )
            )
        }
    }

    // --- Customer Operations ---
    suspend fun addCustomer(customer: Customer): Long = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)

    // --- Sales POS Operations ---
    suspend fun completeSale(
        sale: Sale,
        items: List<SaleItem>,
        isCredit: Boolean,
        dueDateMillis: Long = System.currentTimeMillis() + 14 * 86400000L
    ): Long {
        val saleId = dao.insertSale(sale)
        val itemsWithSaleId = items.map { it.copy(saleId = saleId) }
        dao.insertSaleItems(itemsWithSaleId)

        // Automatically reduce inventory
        for (item in items) {
            val product = dao.getProductById(item.productId)
            if (product != null) {
                val newStock = (product.currentStock - item.quantity).coerceAtLeast(0)
                val status = when {
                    newStock <= 0 -> "Out of Stock"
                    newStock <= product.minStockLevel -> "Low Stock"
                    else -> "In Stock"
                }
                dao.updateProductStock(product.id, newStock, status)
                dao.insertStockAdjustment(
                    StockAdjustment(
                        productId = product.id,
                        productName = product.name,
                        type = "Sale",
                        quantityChange = -item.quantity,
                        previousStock = product.currentStock,
                        newStock = newStock,
                        reason = "Sold via ${sale.saleNumber}"
                    )
                )

                if (newStock <= 0) {
                    dao.insertNotification(
                        NotificationItem(
                            businessId = 1,
                            title = "Out of Stock",
                            message = "${product.name} just ran out of stock after sale ${sale.saleNumber}.",
                            type = "stock"
                        )
                    )
                }
            }
        }

        // Customer balances & Credit handling
        if (sale.customerId != null) {
            val creditAmount = if (isCredit) sale.totalAmount else 0.0
            dao.updateCustomerBalances(sale.customerId, sale.totalAmount, creditAmount)

            if (isCredit) {
                val customer = dao.getCustomerById(sale.customerId)
                val debt = Debt(
                    businessId = 1,
                    customerId = sale.customerId,
                    customerName = sale.customerName,
                    customerPhone = customer?.phone ?: "",
                    saleId = saleId,
                    totalAmount = sale.totalAmount,
                    paidAmount = 0.0,
                    remainingAmount = sale.totalAmount,
                    dueDate = dueDateMillis,
                    status = "Pending",
                    notes = "Credit sale ${sale.saleNumber}"
                )
                dao.insertDebt(debt)
                dao.insertNotification(
                    NotificationItem(
                        businessId = 1,
                        title = "Credit Sale Recorded",
                        message = "Credit sale of ${sale.totalAmount} for ${sale.customerName} due on due date.",
                        type = "debt"
                    )
                )
            }
        }

        return saleId
    }

    // --- Expense Operations ---
    suspend fun addExpense(expense: Expense): Long = dao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)

    // --- Debt & Credit Operations ---
    suspend fun recordDebtPayment(
        debtId: Long,
        paymentAmount: Double,
        method: String,
        reference: String
    ) {
        val debt = dao.getDebtById(debtId) ?: return
        val newPaid = debt.paidAmount + paymentAmount
        val newRemaining = (debt.totalAmount - newPaid).coerceAtLeast(0.0)
        val status = if (newRemaining <= 0.0) "Paid" else "Partially Paid"

        dao.updateDebt(
            debt.copy(
                paidAmount = newPaid,
                remainingAmount = newRemaining,
                status = status
            )
        )
        dao.insertDebtPayment(
            DebtPayment(
                debtId = debtId,
                amount = paymentAmount,
                paymentMethod = method,
                reference = reference
            )
        )
        dao.reduceCustomerDebt(debt.customerId, paymentAmount)

        dao.insertNotification(
            NotificationItem(
                businessId = 1,
                title = "Debt Payment Received",
                message = "Received KES $paymentAmount from ${debt.customerName} via $method (Ref: $reference).",
                type = "payment"
            )
        )
    }

    // --- Invoices ---
    suspend fun createInvoice(invoice: Invoice, items: List<InvoiceItem>): Long {
        val id = dao.insertInvoice(invoice)
        dao.insertInvoiceItems(items.map { it.copy(invoiceId = id) })
        return id
    }

    suspend fun updateInvoice(invoice: Invoice) = dao.updateInvoice(invoice)

    // --- Employees ---
    suspend fun addEmployee(employee: Employee): Long = dao.insertEmployee(employee)
    suspend fun updateEmployee(employee: Employee) = dao.updateEmployee(employee)
    suspend fun deleteEmployee(employee: Employee) = dao.deleteEmployee(employee)

    // --- Notifications ---
    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun markAllNotificationsRead() = dao.markAllNotificationsRead(1)
}
