package com.gunnarro.android.terex.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gunnarro.android.terex.R;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private NavController navController;

    @Inject
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().getWindow().setDecorFitsSystemWindows(false);
        } else {
            View windowDecorView = requireActivity().getWindow().getDecorView();
            windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.btn_timesheet_list_view).setOnClickListener(v -> navController.navigate(R.id.nav_from_home_to_timesheet_list));

        view.findViewById(R.id.btn_invoice_list_view).setOnClickListener(v -> navController.navigate(R.id.nav_from_home_to_invoice_list));

        view.findViewById(R.id.btn_history_view).setOnClickListener(v -> {
        });

        view.findViewById(R.id.btn_settings_view).setOnClickListener(v -> {
        });
        return view;
    }

    /**
     * setup after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(view);
    }
}
