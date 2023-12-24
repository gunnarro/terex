package com.gunnarro.android.terex.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.terex.R;

import org.jetbrains.annotations.NotNull;

abstract class BaseFragment extends Fragment {

    private NavController navController;

    /**
     * setup after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(view);
    }

    protected void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView(), msg, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(bgColor, theme));
        snackbar.show();
    }

    protected void showInfoDialog(String severity, String message) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(severity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    protected void navigateTo(int destinationFragmentId, Bundle bundle) {
        navController.navigate(destinationFragmentId, bundle);
    }

    protected NavController getNavController() {
        return navController;
    }

}
