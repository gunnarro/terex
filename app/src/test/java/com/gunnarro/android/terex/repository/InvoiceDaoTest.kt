package com.gunnarro.android.terex.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gunnarro.android.terex.config.AppDatabase
import com.gunnarro.android.terex.domain.entity.Invoice
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * instrumental test, requires application context
 */

class InvoiceDaoTest {
    private var invoiceDao: InvoiceDao? = null
    private var db: AppDatabase? = null
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Assert.assertEquals("com.gunnarro.android.terex", context.packageName)
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        invoiceDao = db!!.invoiceDao()
    }

    @After
    fun finished() {
        db!!.close()
    }

    @Test
    fun insertInvoice() {
        val invoice = Invoice()
        invoice.invoiceId = 123456789
        invoice.amount = 250.50
        invoice.billingDate = LocalDate.now()
        invoice.vat = 25.00
        invoiceDao!!.insert(invoice)
        val newInvoice = invoiceDao!!.getInvoiceById(invoice.invoiceId)
        Assert.assertTrue(newInvoice.keys.stream().findFirst().isPresent)
    }

}