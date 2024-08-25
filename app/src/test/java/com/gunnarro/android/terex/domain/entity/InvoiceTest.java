package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvoiceTest {

    @Test
    void isNew() {
        Invoice invoice = new Invoice();
        assertTrue(invoice.isNew());
    }

    @Test
    void areEqual() {
        Invoice invoice1 = new Invoice();
        invoice1.setTimesheetId(22L);
        invoice1.setRecipientId(44L);

        Invoice invoice2 = new Invoice();
        invoice2.setTimesheetId(22L);
        invoice2.setRecipientId(44L);

        assertTrue(invoice1.equals(invoice2));
    }

    @Test
    void areNotEqual() {
        Invoice invoice1 = new Invoice();
        invoice1.setTimesheetId(22L);
        invoice1.setRecipientId(44L);

        Invoice invoice2 = new Invoice();
        invoice1.setTimesheetId(22L);
        invoice1.setRecipientId(55L);

        assertFalse(invoice1.equals(invoice2));
    }
}
