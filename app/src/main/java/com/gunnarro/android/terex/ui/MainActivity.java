package com.gunnarro.android.terex.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * <a href="https://guides.codepath.com/android/Using-the-App-Toolbar">See Using-the-App-Toolbar</a>
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST = 1;

    private AppBarConfiguration appBarConfiguration;

    public MainActivity() {
        // left empty
    }

    public static String getInternalAppDir() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "context: " + getApplicationContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "app file dir: " + getApplicationContext().getFilesDir().getPath());
        // init database
        AppDatabase.init(getApplicationContext());

        if (!new File(getApplicationContext().getFilesDir().getPath()).exists()) {
            Log.d(Utility.buildTag(getClass(), "onCreate"), "app file dir missing! " + getApplicationContext().getFilesDir().getPath());
        }

        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            Log.e(Utility.buildTag(getClass(), "onCreate"), "Failed starting! " + e.getMessage());
            throw new TerexApplicationException("Application error!", "50000", e);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_content_frame);
        NavController navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener(super::onSupportNavigateUp)
                .build();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        // In oder to add navigation support as default to the action bar, must also override onSupportNavigateUp() method.
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        // Finally, check and grant or deny permissions
        checkPermissions();
    }

    /**
     * Override onSupportNavigateUp() to handle Up navigation
     */

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_content_frame);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.app_bar_home).setOnMenuItemClickListener(item -> {
            Navigation.findNavController(this, R.id.nav_content_frame).navigate(R.id.nav_to_home);
            return true;
        });
        return true;
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

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void showUpButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeActionContentDescription(R.string.title_home);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.outline_home_24);
            getSupportActionBar().setHomeActionContentDescription("Home");
        }
    }

    public void hideUpButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    // https://gist.github.com/EduardoSP6/29c5388617b863503a2b0a9bfdb1dbdd
    public void onBackStackChanged() {
        // enable Up button only if there are entries on the backstack
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            hideUpButton();
        } else {
            showUpButton();
        }
    }
}