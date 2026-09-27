package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class Business(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val currency: String = "KES",
    val location: String = "Nairobi, Kenya",
    val phone: String = "+254 700 123456",
    val email: String = "contact@techpoint.co.ke",
    val taxRate: Double = 16.0,
    val invoicePrefix: String = "BIZ",
    val mpesaTillOrPaybill: String = "542890",
    val mpesaType: String = "Till (Buy Goods)",
    val subscriptionPlan: String = "Business",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val name: String,
    val sku: String,
    val category: String,
    val buyingPrice: Double,
    val sellingPrice: Double,
    val currentStock: Int,
    val minStockLevel: Int = 5,
    val supplier: String = "Direct Import",
    val description: String = "",
    val status: String = "In Stock",
    val isArchived: Boolean = false
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "Nairobi",
    val customerType: String = "Retail",
    val totalPurchases: Double = 0.0,
    val outstandingBalance: Double = 0.0,
    val notes: String = ""
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val saleNumber: String,
    val customerId: Long? = null,
    val customerName: String = "Walk-in Customer",
    val subtotal: Double,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double,
    val paymentMethod: String, // Cash, M-PESA, Bank, Card, Credit
    val paymentStatus: String, // Paid, Partial, Credit
    val mpesaCode: String = "",
    val employeeName: String = "Owner",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_items")
data class SaleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val title: String,
    val category: String,
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val paymentMethod: String = "M-PESA",
    val description: String = ""
)

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val customerId: Long,
    val customerName: String,
    val customerPhone: String,
    val saleId: Long? = null,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double,
    val dueDate: Long,
    val status: String = "Pending", // Pending, Partially Paid, Paid, Overdue
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "debt_payments")
data class DebtPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val debtId: Long,
    val amount: Double,
    val paymentMethod: String = "M-PESA",
    val reference: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val invoiceNumber: String,
    val customerId: Long,
    val customerName: String,
    val customerEmail: String = "",
    val customerPhone: String = "",
    val subtotal: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val issueDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + 14 * 86400000L,
    val status: String = "Sent", // Draft, Sent, Paid, Overdue
    val notes: String = "Payment due within 14 days via M-PESA or Bank Transfer."
)

@Entity(tableName = "invoice_items")
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val amount: Double
)

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // Owner, Manager, Salesperson, Accountant, Staff
    val status: String = "Active"
)

@Entity(tableName = "stock_adjustments")
data class StockAdjustment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // Stock In, Stock Out, Recount, Damaged
    val quantityChange: Int,
    val previousStock: Int,
    val newStock: Int,
    val reason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1,
    val title: String,
    val message: String,
    val type: String, // stock, debt, invoice, payment, alert
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
