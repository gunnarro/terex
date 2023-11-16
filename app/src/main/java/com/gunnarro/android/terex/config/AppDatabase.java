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
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceFile;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.RecruitmentCompany;
import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.repository.InvoiceDao;
import com.gunnarro.android.terex.repository.ProjectDao;
import com.gunnarro.android.terex.repository.TimesheetDao;
import com.gunnarro.android.terex.repository.TimesheetEntryDao;
import com.gunnarro.android.terex.repository.TimesheetSummaryDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 */
@Database(entities = {
        Timesheet.class,
        TimesheetEntry.class,
        Invoice.class,
        TimesheetSummary.class,
        Project.class,
        InvoiceFile.class
}, version = 15, views = {TimesheetView.class})
public abstract class AppDatabase extends RoomDatabase {
    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 10;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Allow only single thread access to the database
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "terex_database")
                            .fallbackToDestructiveMigration()
                            //.createFromAsset("database/terex_database_data.sqlite")
                           // .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TimesheetDao timesheetDao();

    public abstract TimesheetEntryDao timesheetEntryDao();

    public abstract TimesheetSummaryDao timesheetSummaryDao();

    public abstract InvoiceDao invoiceDao();

    public abstract ProjectDao projectDao();


    //public abstract RecruitmentDao recruitmentDao();

    // Called when the database is created for the first time. This is called after all the tables are created.
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("RoomDatabase.Callback", "start init database");
            // Create recruitment companies
            RecruitmentCompany recruitmentCompany = new RecruitmentCompany();
            Company company = new Company();
            company.setName("Norway Consulting AS");
            company.setOrganizationNumber("");
           /* Address address = new Address();
            address.setStreetName("Grensen");
            address.setStreetNumber("16");
            address.setPostCode("0159");
            address.setCountry("Norway");
*/

            // this method is called when database is created
            // and below line is to populate our data.
            // new PopulateDbAsyncTask(INSTANCE).execute();
            RegisterWork work = RegisterWork.buildDefault("MasterCard");
            TimesheetEntry timesheet = new TimesheetEntry();
            timesheet.setTimesheetId(1L);
            timesheet.setStatus(work.getStatus());
            timesheet.setHourlyRate(work.getHourlyRate());
            timesheet.setBreakInMin(work.getBreakInMin());
            timesheet.setWorkdayDate(work.getWorkdayDate());
            timesheet.setFromTime(work.getFromTime());
            timesheet.setToTime(work.getToTime());
         //   INSTANCE.timesheetEntryDao().insert(timesheet);
        }
    };

    // we are creating an async task class to perform task in background.
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(AppDatabase instance) {
            RegisterWork work = RegisterWork.buildDefault("MasterCard");
            TimesheetEntry timesheetEntry = new TimesheetEntry();
            timesheetEntry.setTimesheetId(1L);
            timesheetEntry.setStatus(work.getStatus());
            timesheetEntry.setHourlyRate(work.getHourlyRate());
            timesheetEntry.setBreakInMin(work.getBreakInMin());
            timesheetEntry.setWorkdayDate(work.getWorkdayDate());
            timesheetEntry.setFromTime(work.getFromTime());
            timesheetEntry.setToTime(work.getToTime());
            instance.timesheetEntryDao().insert(timesheetEntry);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}