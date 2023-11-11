package com.gunnarro.android.terex.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.ui.fragment.AdminFragment;
import com.gunnarro.android.terex.ui.fragment.InvoiceListFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryAddFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryCustomCalendarFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetListFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryListFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetNewFragment;
import com.gunnarro.android.terex.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int PERMISSION_REQUEST = 1;

    @Inject
    AdminFragment adminFragment;

    @Inject
    TimesheetEntryListFragment timesheetListFragment;

    @Inject
    TimesheetListFragment timesheetFragment;

    @Inject
    TimesheetEntryAddFragment timesheetAddEntryFragment;

    @Inject
    TimesheetEntryCustomCalendarFragment timesheetCalendarFragment;

    @Inject
    TimesheetNewFragment timesheetNewFragment;

    @Inject
    InvoiceListFragment invoiceListFragment;

    private DrawerLayout drawer;

    public MainActivity() {
        this.adminFragment = new AdminFragment();
        this.timesheetListFragment = new TimesheetEntryListFragment();
        //this.timesheetAddEntryFragment = new TimesheetAddEntryFragment();
        this.invoiceListFragment = new InvoiceListFragment();
        //this.timesheetCalendarFragment = new TimesheetCustomCalendarFragment();
        //this.timesheetNewFragment = new TimesheetNewFragment();
        this.timesheetFragment = new TimesheetListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "context: " + getApplicationContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "app file dir: " + getApplicationContext().getFilesDir().getPath());

        // Initialize exception handler
        //new UCEHandler.Builder(this).build();

        if (!new File(getApplicationContext().getFilesDir().getPath()).exists()) {
            Log.d(Utility.buildTag(getClass(), "onCreate"), "app file dir missing! " + getApplicationContext().getFilesDir().getPath());
        }

        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Utility.buildTag(getClass(), "onCreate"), "Failed starting! " + e.getMessage());
        }
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.title_timesheet, R.string.title_timesheet);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        // display home button for actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // navigation view select timesheet menu as default
        navigationView.setCheckedItem(R.id.nav_timesheet_list);

        if (savedInstanceState == null) {
            viewFragment(timesheetFragment);
        }
        // Finally, check and grant or deny permissions
        checkPermissions();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(Utility.buildTag(getClass(), "onOptionsItemSelected"), "selected: " + item);
        if (item.getItemId() == android.R.id.home) {// Open Close Drawer Layout
            if (drawer.isOpen()) {
                drawer.closeDrawers();
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            Log.d("MainActivity.onNavigationItemSelected", "selected: " + menuItem.getItemId());
            int id = menuItem.getItemId();
            if (id == R.id.nav_timesheet_list) {
                viewFragment(timesheetFragment);
                setTitle(R.string.title_timesheets);
            } else if (id == R.id.nav_invoice_list) {
                viewFragment(invoiceListFragment);
                setTitle(R.string.title_invoice);
            } else if (id == R.id.nav_admin) {
                viewFragment(adminFragment);
                setTitle(R.string.title_admin);
            }
            /*
            else if (id == R.id.nav_timesheet_new) {
                viewFragment(timesheetNewFragment);
                setTitle(R.string.title_timesheet_new);
            }
            else if (id == R.id.nav_invoice) {
                viewFragment(invoiceFragment);
                setTitle(R.string.title_invoice_overview);
            } else if (id == R.id.nav_timesheet_calendar) {
                viewFragment(timesheetCalendarFragment);
                setTitle(R.string.title_timesheet_calendar);
            }   else if (id == R.id.nav_timesheet_register_work) {
                viewFragment(timesheetAddEntryFragment);
                setTitle(R.string.title_register_work);
            } */
            // close drawer after clicking the menu item
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void viewFragment(@NonNull Fragment fragment) {
        Log.d(Utility.buildTag(getClass(), "viewFragment"), "fragment tag: " + fragment.getTag());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, fragment.getTag())
                .commit();
    }

    private void checkPermissions() {
        Log.i(Utility.buildTag(getClass(), "checkPermissions"), "Start check permissions...");
        // check and ask user for permission if not granted
        String[] permissions = new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS};
        for (String permission : permissions) {
            if (super.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Log.i(Utility.buildTag(getClass(), "checkPermissions"), String.format("Not Granted, send request: %s", permission));
                super.requestPermissions(new String[]{permission}, PERMISSION_REQUEST);
            } else {
                // show dialog explaining why this permission is needed
                if (super.shouldShowRequestPermissionRationale(permission)) {
                    Log.i(Utility.buildTag(getClass(), "checkPermissions"), "explain why we need this permission! permission: " + permission);
                }
            }
        }
    }

    /**
     * This function is called when user accept or decline the permission.
     * Request Code is used to check which permission called this function.
     * This request code is provided when user is prompt for permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(Utility.buildTag(getClass(), "onRequestPermissions"), String.format("requestCode=%s, permission=%s, grantResult=%s", requestCode, new ArrayList<>(Arrays.asList(permissions)), new ArrayList<>(Collections.singletonList(grantResults))));
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Utility.buildTag(getClass(), "onRequestPermissions"), String.format("permission granted for permission: %s", Arrays.asList(permissions)));
            } else {
                Log.i(Utility.buildTag(getClass(), "onRequestPermissions"), String.format("permission denied for permission: %s", Arrays.asList(permissions)));
            }
        }
    }
}