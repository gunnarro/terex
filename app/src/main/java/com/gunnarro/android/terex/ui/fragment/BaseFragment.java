package com.gunnarro.android.terex.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

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

    // -------------------------------------
    // common data input validation
    // -------------------------------------

    /**
     * used to check if a input field is empty or not
     * @param e inputfield to check
     * @return true if not empty, otherwise false
     */
    protected boolean hasText(Editable e) {
        return e != null && !e.toString().isBlank();
    }

    /**
     * Used for input validation
     */
    protected TextWatcher createEmptyTextValidator(EditText editText, String regexp, String validationErrorMsg) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().matches(regexp)) {
                    editText.setError(validationErrorMsg);
                }
            }
        };
    }

}
