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
import com.gunnarro.android.terex.ui.fragment.InvoiceFragment;
import com.gunnarro.android.terex.ui.fragment.InvoiceListFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetListFragment;
import com.gunnarro.android.terex.ui.fragment.TimesheetRegisterWorkFragment;
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
    TimesheetListFragment timesheetListFragment;

    @Inject
    TimesheetRegisterWorkFragment timesheetRegisterWorkFragment;

    @Inject
    InvoiceListFragment invoiceListFragment;

    @Inject
    InvoiceFragment invoiceFragment;

    private DrawerLayout drawer;

    public MainActivity() {
        this.adminFragment = new AdminFragment();
        this.timesheetListFragment = new TimesheetListFragment();
        this.timesheetRegisterWorkFragment = new TimesheetRegisterWorkFragment();
        this.invoiceListFragment = new InvoiceListFragment();
        this.invoiceFragment = new InvoiceFragment();
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
            viewFragment(timesheetListFragment);
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
            if (id == R.id.nav_timesheet_register_work) {
                setTitle(R.string.title_register_work);
                viewFragment(timesheetRegisterWorkFragment);
            } else if (id == R.id.nav_timesheet_list) {
                setTitle(R.string.title_timesheet);
                viewFragment(timesheetListFragment);
            } else if (id == R.id.nav_invoice_list) {
                setTitle(R.string.title_invoice);
                viewFragment(invoiceListFragment);
            } else if (id == R.id.nav_invoice) {
                setTitle(R.string.title_invoice_overview);
                viewFragment(invoiceFragment);
            } else if (id == R.id.nav_admin) {
                setTitle(R.string.title_timesheet);
                viewFragment(adminFragment);
            }
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
        Log.d(Utility.buildTag(getClass(), "viewFragment"), "fragment: " + fragment.getTag());
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