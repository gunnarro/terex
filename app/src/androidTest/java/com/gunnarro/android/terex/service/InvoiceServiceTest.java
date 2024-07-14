package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertNull;

import com.gunnarro.android.terex.IntegrationTestSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InvoiceServiceTest extends IntegrationTestSetup {

    private InvoiceService invoiceService;

    @Before
    public void setup() {
        super.setupDatabase();
        invoiceService = new InvoiceService();
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Test
    public void getInvoice_not_found() {
       assertNull(invoiceService.getInvoiceDto(2334L));
    }
}
