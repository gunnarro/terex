package com.gunnarro.android.terex.utility;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PdfUtility {

    public static boolean htmlToPdf(String html, String fileName) {
        String pdfFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf";
        String htmlFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".html";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(path, pdfFileName);
        File htmlFile = new File(path, htmlFileName);
        try {

            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(html.getBytes());
            fos.close();

            fos = new FileOutputStream(htmlFile);
            fos.write(html.getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e("Error convert html to pdf!", e.getMessage());
            return false;
        }
    }
}

