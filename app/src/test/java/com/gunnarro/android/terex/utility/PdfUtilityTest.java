package com.gunnarro.android.terex.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;

import android.os.Environment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@Disabled
@ExtendWith(MockitoExtension.class)
public class PdfUtilityTest {

    @Mock
    Environment environment;

    @Test
    void htmlToPdf() {
        String htmlStr = "<h1>Html to pdf test</h1>";
        assertTrue(PdfUtility.htmlToPdf(htmlStr, "htmlToPdfTest.pdf"));
    }
}
