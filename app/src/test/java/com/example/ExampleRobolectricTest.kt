package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Bizly app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bizly", appName)
    }

    @Test
    fun `test sales calculation and VAT margin`() {
        val unitPrice = 21500.0
        val quantity = 2
        val subtotal = unitPrice * quantity
        val discountPercent = 10
        val discountAmount = subtotal * (discountPercent / 100.0)
        val grandTotal = subtotal - discountAmount

        assertEquals(43000.0, subtotal, 0.01)
        assertEquals(4300.0, discountAmount, 0.01)
        assertEquals(38700.0, grandTotal, 0.01)

        val buyingPrice = 16500.0
        val grossMargin = ((unitPrice - buyingPrice) / unitPrice) * 100.0
        assertTrue(grossMargin > 20.0)
    }

    @Test
    fun `test debt remaining balance calculation`() {
        val totalCredit = 45000.0
        val initialPayment = 15000.0
        val remaining = totalCredit - initialPayment
        assertEquals(30000.0, remaining, 0.01)

        val secondPayment = 30000.0
        val finalBalance = remaining - secondPayment
        assertEquals(0.0, finalBalance, 0.01)
    }
}
