package com.gunnarro.android.terex.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;

import android.os.Environment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

//@Disabled
@ExtendWith(MockitoExtension.class)
class PdfUtilityTest {

    @Mock
    Environment environment;

    @Test
    void htmlToPdf() {
        String htmlStr = "<h1>Html to pdf test</h1>";
        assertTrue(PdfUtility.saveFile(htmlStr, "htmlToPdfTest.pdf"));
    }

    @Test
    void htmlToPdf2() throws IOException {
        String htmlStr = "<h1>Html to pdf test</h1>";

       //PdfUtility.toPdf(htmlStr, "htmlToPdfTest2.pdf");
    }
}
