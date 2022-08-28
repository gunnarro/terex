package com.gunnarro.android.terex.config;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gunnarro.android.terex.domain.dbview.TimesheetView;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.repository.InvoiceDao;
import com.gunnarro.android.terex.repository.TimesheetDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 */
@Database(entities = {Timesheet.class, Invoice.class, InvoiceSummary.class}, version = 16, views = {TimesheetView.class})
public abstract class AppDatabase extends RoomDatabase {
    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Allow only single single thread access to the database
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "terex_database")
                            .fallbackToDestructiveMigration()
                          //  .createFromAsset("database/terex_database_data.sqlite")
                          //  .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TimesheetDao timesheetDao();

    public abstract InvoiceDao invoiceDao();

    // Called when the database is created for the first time. This is called after all the tables are created.
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("RoomDatabase.Callback", "start init database");
            // this method is called when database is created
            // and below line is to populate our data.
           // new PopulateDbAsyncTask(INSTANCE).execute();
            RegisterWork work = RegisterWork.buildDefault("MasterCard");
            Timesheet timesheet = new Timesheet();
            timesheet.setStatus(work.getStatus());
            timesheet.setClientName(work.getClientName());
            timesheet.setProjectName(work.getProjectName());
            timesheet.setHourlyRate(work.getHourlyRate());
            timesheet.setBreakInMin(work.getBreakInMin());
            timesheet.setWorkdayDate(work.getWorkdayDate());
            timesheet.setFromTime(work.getFromTime());
            timesheet.setToTime(work.getToTime());
            INSTANCE.timesheetDao().insert(timesheet);
        }
    };

    // we are creating an async task class to perform task in background.
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(AppDatabase instance) {
            RegisterWork work = RegisterWork.buildDefault("MasterCard");
            Timesheet timesheet = new Timesheet();
            timesheet.setStatus(work.getStatus());
            timesheet.setClientName(work.getClientName());
            timesheet.setProjectName(work.getProjectName());
            timesheet.setHourlyRate(work.getHourlyRate());
            timesheet.setBreakInMin(work.getBreakInMin());
            timesheet.setWorkdayDate(work.getWorkdayDate());
            timesheet.setFromTime(work.getFromTime());
            timesheet.setToTime(work.getToTime());
            instance.timesheetDao().insert(timesheet);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}