package com.gunnarro.android.terex.utility;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PdfUtility {

    public static boolean saveFile(String html, String filePath) {
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(html.getBytes());
            fos.close();
            Log.d("saved file", file.getPath());
            return true;
        } catch (IOException e) {
            Log.e("Error convert html to pdf!", e.getMessage());
            return false;
        }
    }

    /**
     * Read the file and returns the byte array
     *
     * @param fileName name of file
     * @return the bytes of the file
     */
    public static String readFile(String fileName) throws FileNotFoundException {
        StringBuilder invoiceHtml = new StringBuilder();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try (InputStream fis = new FileInputStream(new File(path + "/" + fileName));
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(invoiceHtml::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return invoiceHtml.toString();
    }
}

