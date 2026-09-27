package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

@Dao
interface BizlyDao {

    // --- Business ---
    @Query("SELECT * FROM businesses WHERE id = :id LIMIT 1")
    fun getBusinessFlow(id: Long = 1): Flow<Business?>

    @Query("SELECT * FROM businesses WHERE id = :id LIMIT 1")
    suspend fun getBusiness(id: Long = 1): Business?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business): Long

    @Update
    suspend fun updateBusiness(business: Business)

    // --- Products ---
    @Query("SELECT * FROM products WHERE businessId = :businessId AND isArchived = 0 ORDER BY name ASC")
    fun getProducts(businessId: Long = 1): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET currentStock = :newStock, status = :status WHERE id = :productId")
    suspend fun updateProductStock(productId: Long, newStock: Int, status: String)

    // --- Customers ---
    @Query("SELECT * FROM customers WHERE businessId = :businessId ORDER BY name ASC")
    fun getCustomers(businessId: Long = 1): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<Customer>)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("UPDATE customers SET totalPurchases = totalPurchases + :addedPurchase, outstandingBalance = outstandingBalance + :addedBalance WHERE id = :customerId")
    suspend fun updateCustomerBalances(customerId: Long, addedPurchase: Double, addedBalance: Double)

    @Query("UPDATE customers SET outstandingBalance = outstandingBalance - :paidAmount WHERE id = :customerId")
    suspend fun reduceCustomerDebt(customerId: Long, paidAmount: Double)

    // --- Sales & Sale Items ---
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getSales(businessId: Long = 1): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): Sale?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<Sale>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItems(saleId: Long): Flow<List<SaleItem>>

    @Query("SELECT * FROM sale_items")
    fun getAllSaleItems(): Flow<List<SaleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItem>)

    // --- Expenses ---
    @Query("SELECT * FROM expenses WHERE businessId = :businessId ORDER BY dateMillis DESC")
    fun getExpenses(businessId: Long = 1): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Debts & Debt Payments ---
    @Query("SELECT * FROM debts WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getDebts(businessId: Long = 1): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE id = :id LIMIT 1")
    suspend fun getDebtById(id: Long): Debt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<Debt>)

    @Update
    suspend fun updateDebt(debt: Debt)

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY dateMillis DESC")
    fun getDebtPayments(debtId: Long): Flow<List<DebtPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtPayment(payment: DebtPayment): Long

    // --- Invoices & Items ---
    @Query("SELECT * FROM invoices WHERE businessId = :businessId ORDER BY issueDate DESC")
    fun getInvoices(businessId: Long = 1): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Long): Invoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<Invoice>)

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    fun getInvoiceItems(invoiceId: Long): Flow<List<InvoiceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItem>)

    // --- Employees ---
    @Query("SELECT * FROM employees WHERE businessId = :businessId ORDER BY name ASC")
    fun getEmployees(businessId: Long = 1): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    // --- Stock Adjustments ---
    @Query("SELECT * FROM stock_adjustments ORDER BY createdAt DESC")
    fun getStockAdjustments(): Flow<List<StockAdjustment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockAdjustment(adjustment: StockAdjustment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockAdjustments(adjustments: List<StockAdjustment>)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getNotifications(businessId: Long = 1): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationItem>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE businessId = :businessId")
    suspend fun markAllNotificationsRead(businessId: Long = 1)

    // --- Clear for demo reset ---
    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("DELETE FROM customers")
    suspend fun clearCustomers()

    @Query("DELETE FROM sales")
    suspend fun clearSales()

    @Query("DELETE FROM sale_items")
    suspend fun clearSaleItems()

    @Query("DELETE FROM expenses")
    suspend fun clearExpenses()

    @Query("DELETE FROM debts")
    suspend fun clearDebts()

    @Query("DELETE FROM debt_payments")
    suspend fun clearDebtPayments()

    @Query("DELETE FROM invoices")
    suspend fun clearInvoices()

    @Query("DELETE FROM invoice_items")
    suspend fun clearInvoiceItems()

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM stock_adjustments")
    suspend fun clearStockAdjustments()
}
