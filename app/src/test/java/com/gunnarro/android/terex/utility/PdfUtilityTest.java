package com.gunnarro.android.terex.utility;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PdfUtilityTest {

    @Test
    void htmlToPdf() throws IOException {
        String htmlStr = "<h1>Html to pdf test</h1>";
        PdfUtility.htmlToPdf(htmlStr, "htmlToPdfTest.pdf");
    }
}
