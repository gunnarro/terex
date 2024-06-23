package com.gunnarro.android.terex.integration.jira;

import com.google.gson.Gson;
import com.gunnarro.android.terex.integration.jira.model.Workload;
import com.gunnarro.android.terex.service.InvoiceService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * https://apidocs.tempo.io/
 */
public class TempoApi {

    private final static String WORKLOAD_JSON_FILE="integration/jira/tempo-schema.json";

    Gson gson = new Gson();

    public Workload getWorkload() throws IOException {
        String workloadJson = readFile("src/main/assets/" + WORKLOAD_JSON_FILE);
        return gson.fromJson(workloadJson, Workload.class);
    }

    private String readFile(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), "UTF-8");
    }
}
