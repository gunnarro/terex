package com.gunnarro.android.terex.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PdfUtilityTest {

    @Test
    void htmlToPdf() {
        String htmlStr = "<h1>Html to pdf test</h1>";
        assertTrue(PdfUtility.saveFile(htmlStr, "htmlToPdfTest.pdf"));
    }

}
