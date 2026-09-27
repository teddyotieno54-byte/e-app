package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BizlyDao
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

@Database(
    entities = [
        Business::class,
        Product::class,
        Customer::class,
        Sale::class,
        SaleItem::class,
        Expense::class,
        Debt::class,
        DebtPayment::class,
        Invoice::class,
        InvoiceItem::class,
        Employee::class,
        StockAdjustment::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bizlyDao(): BizlyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bizly_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
