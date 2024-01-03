package com.gunnarro.android.terex.config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DbMigrationFrom1To2 extends Migration {

    private final Context context;
    public DbMigrationFrom1To2(Context context, int startVersion, int endVersion) {
        super(startVersion, endVersion);
        this.context = context;
    }

    /**
     * Add your SQL statements to update the schema here.
     */
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        List<String> sqlQueryList = readMigrationSqlQueryFile(startVersion, endVersion);
        sqlQueryList.forEach( query -> {
            Log.d("", query);
            database.execSQL(query);
        });
        Log.i("", String.format("executed database schema migration from version %s to %s", startVersion, endVersion));
    }

    private List<String> readMigrationSqlQueryFile(int startVersion, int endVersion) {
        List<String> sqlQueryList = new ArrayList<>();
        String migrationFilePath = String.format("database/migration/db_migration_from_v%s_to_v%s.sql", startVersion, endVersion);
        Log.d("", String.format("read sql db migration file, file=%s", migrationFilePath));
        try (InputStream fis = context.getAssets().open(migrationFilePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(query -> {
                // skip comments
                if (!query.startsWith("--") && !query.isBlank()) {
                    sqlQueryList.add(query);
                }
            });
        } catch (IOException e) {
            throw new TerexApplicationException(String.format("ApplicationError! error reading migration file! file=%s", migrationFilePath), "50050", e);
        }
        return sqlQueryList;
    }
}