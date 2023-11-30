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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;

//import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor;
//import org.jsoup.nodes.Document;
//import org.xhtmlrenderer.pdf.ITextRenderer;

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
/*
    public static void htmlToPdf(String html, String pdfFileName) throws IOException {
        Log.d("htmlToPdf", pdfFileName);
        String xhtml = htmlToXhtml(html);
        xhtmlToPdf(xhtml, pdfFileName);
    }

    private static void xhtmlToPdf(String xhtml, String outFileName) throws IOException {
        File output = new File(outFileName);
        ITextRenderer iTextRenderer = new ITextRenderer();
        iTextRenderer.setDocumentFromString(xhtml);
        iTextRenderer.layout();
        OutputStream os = new FileOutputStream(output);
        iTextRenderer.createPDF(os);
        os.close();
    }

    private static String htmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

 */
}

