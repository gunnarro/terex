package com.gunnarro.android.terex.config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gunnarro.android.terex.domain.dbview.TimesheetView;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.AddressDao;
import com.gunnarro.android.terex.repository.ClientDao;
import com.gunnarro.android.terex.repository.ContactInfoDao;
import com.gunnarro.android.terex.repository.IntegrationDao;
import com.gunnarro.android.terex.repository.InvoiceAttachmentDao;
import com.gunnarro.android.terex.repository.InvoiceDao;
import com.gunnarro.android.terex.repository.OrganizationDao;
import com.gunnarro.android.terex.repository.PersonDao;
import com.gunnarro.android.terex.repository.ProjectDao;
import com.gunnarro.android.terex.repository.TimesheetDao;
import com.gunnarro.android.terex.repository.TimesheetEntryDao;
import com.gunnarro.android.terex.repository.TimesheetSummaryDao;
import com.gunnarro.android.terex.repository.UserAccountDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 */
@Database(entities = {
        UserAccount.class,
        Timesheet.class,
        TimesheetEntry.class,
        Invoice.class,
        TimesheetSummary.class,
        Project.class,
        ContactInfo.class,
        Address.class,
        Person.class,
        Organization.class,
        InvoiceAttachment.class,
        Client.class,
        Integration.class,
}, version = AppDatabase.DB_SCHEMA_VERSION, views = {TimesheetView.class}, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    // Increase the version with one if the database schema has changed.
    public static final int DB_SCHEMA_VERSION = 4;
    // marking the instance as volatile to ensure atomic access to the variable
    // The Java volatile keyword guarantees visibility of changes to variables across threads
    private static AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 10;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    protected AppDatabase() {
        super();
    }

    public static void init(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, "terex_database")
                    // use this during development
                   // .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .createFromAsset("database/terex_init_data.db")
                    .addMigrations(getMigration(context, DB_SCHEMA_VERSION - 1, DB_SCHEMA_VERSION))
                   // .addCallback(roomCallback)
                    .build();
        }
    }

    /**
     * This method must be synchronized because all services will try to get an da instance during application startup,
     * and DB config must only be initialized once.
     * If it is acceptable to lose existing data when a migration path is missing, call the fallbackToDestructiveMigration()
     */
    public static synchronized AppDatabase getDatabase() {
        if (INSTANCE == null) {
            throw new TerexApplicationException("Database not initialized. You should call AppDatabase.init(context) in the MainActivity.", "50050", null);
        }
        return INSTANCE;
    }

    public static Migration getMigration(Context applicationContext, int startVersion, int endVersion) {
        return new DbMigration(applicationContext, startVersion, endVersion);
    }

    public abstract UserAccountDao userAccountDao();

    public abstract TimesheetDao timesheetDao();

    public abstract TimesheetEntryDao timesheetEntryDao();

    public abstract TimesheetSummaryDao timesheetSummaryDao();

    public abstract InvoiceDao invoiceDao();

    public abstract ProjectDao projectDao();

    public abstract InvoiceAttachmentDao invoiceAttachmentDao();

    public abstract OrganizationDao organizationDao();

    public abstract ClientDao clientDao();

    public abstract PersonDao personDao();

    public abstract AddressDao addressDao();

    public abstract ContactInfoDao contactInfoDao();

    public abstract IntegrationDao integrationDao();
    /**
     * Called when the database is created for the first time. This is called after all the tables are created.
     */
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {

        /**
         * This method is only called once when the database is first created and then never again as long as the database exists.
         */
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);
            //database.beginTransaction();
            // sql queries for default data
            //database.execSQL("INSERT INTO user_account (id, uuid, created_date, last_modified_date, user_name, password, account_type, organization_id, person_id) VALUES (2001, '', current_timestamp, current_timestamp, 'guro', 'change-me', 'BUSINESS', 1100, null )");
            //database.endTransaction();
            Log.d("RoomDatabase.Callback.onCreate", "start init database");
        }

        /**
         * This method is called every time the database is opened.
         */
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);
            //database.beginTransaction();
            // sql queries for default data
            // database.execSQL("INSERT INTO user_account (id, uuid, created_date, last_modified_date, user_name, password, account_type, organization_id, person_id) VALUES (2001, '', current_timestamp, current_timestamp, 'guro', 'change-me', 'BUSINESS', 1100, null )");
            // database.isDatabaseIntegrityOk();
           /* ContentValues contentValues = new ContentValues();
            contentValues.put("id", 100L);
            contentValues.put("user_name", "testing");
            database.insert("user_account", SQLiteDatabase.CONFLICT_FAIL, contentValues);
            database.endTransaction();*/
            Log.d("RoomDatabase.Callback.onOpen", "start init database");
        }
    };
}