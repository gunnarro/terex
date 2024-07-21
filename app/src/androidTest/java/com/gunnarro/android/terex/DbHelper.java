package com.gunnarro.android.terex;

import android.content.Context;

import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    public static List<String> readMigrationSqlQueryFile(Context context, String sqlFilePath) {
        List<String> sqlQueryList = new ArrayList<>();
        try (InputStream fis = context.getAssets().open(sqlFilePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(query -> {
                // skip comments
                if (!query.startsWith("--") && !query.isBlank()) {
                    sqlQueryList.add(query);
                }
            });
        } catch (IOException e) {
            throw new TerexApplicationException(String.format("ApplicationError! error reading sql file! file=%s", sqlFilePath), "50050", e);
        }
        return sqlQueryList;
    }
}
