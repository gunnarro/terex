package com.gunnarro.android.terex.utility;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PdfUtility {

    public static String getLocalDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    public static boolean saveFile(String html, String filePath) {
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(html.getBytes(StandardCharsets.UTF_8));
            fos.close();
            Log.d("saveFile", String.format("saved file, org, %s", filePath));
            Log.d("saveFile", String.format("saved file, system, %s", file.getPath()));
            return true;
        } catch (IOException e) {
            Log.e("Error convert html to pdf!", e.getMessage());
            return false;
        }
    }

    /**
     * Read the file and returns the byte array
     *
     * @param filePath name of file
     * @return the bytes of the file
     */
    public static String readFile(String filePath) {
        StringBuilder invoiceHtml = new StringBuilder();
        Log.d("", "read html file, path=" + filePath);
        try (InputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(invoiceHtml::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return invoiceHtml.toString();
    }
}

