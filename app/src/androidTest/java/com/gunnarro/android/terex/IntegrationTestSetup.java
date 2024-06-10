package com.gunnarro.android.terex;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.gunnarro.android.terex.config.AppDatabase;

import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public abstract class IntegrationTestSetup {

    private AppDatabase appDatabase;
    private Context appContext;

    public void setupDatabase() {
        appContext = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
    }

    public void cleanUpDatabase() {
        loadTestData();
    }

    public void loadTestData() {
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        appDatabase.getOpenHelper().getWritableDatabase().beginTransaction();
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        appDatabase.getOpenHelper().getWritableDatabase().endTransaction();
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }
}
