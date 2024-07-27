package com.gunnarro.android.terex.integration.jira;

import com.google.gson.Gson;
import com.gunnarro.android.terex.integration.jira.model.Workload;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tempo import/export csv file format:
 * Issue Key, Date Started, Time Spent, Comment
 * NGEN-1,2020-09-16 09:00:00Z,2h,test
 * NGEN-2,2020-09-14 09:00:00Z,2h 30m,comment with spaces
 * NGEN-3,,1h,test
 * <p>
 * <p>
 * https://apidocs.tempo.io/
 */
public class TempoApi {

    private static final String WORKLOAD_JSON_FILE = "integration/jira/tempo-schema.json";
    private static final String WORKLOAD_CSV_FILE = "integration/jira/tempo-timesheet-export.csv";

    Gson gson = new Gson();

    public Workload getTimesheetWorkload() throws IOException {
        String workloadJson = readFile("src/main/assets/" + WORKLOAD_JSON_FILE);
        return gson.fromJson(workloadJson, Workload.class);
    }

    public List<String> getTimesheetExportCvs() throws IOException {
        List<String> lines = readFileLines("src/main/assets/" + WORKLOAD_CSV_FILE);
        // filter out file header
        return lines.stream().filter(l -> !l.startsWith("#")).collect(Collectors.toList());
    }

    private String readFile(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
    }

    private List<String> readFileLines(String filePath) throws IOException {
        return FileUtils.readLines(new File(filePath), StandardCharsets.UTF_8);
    }
}
