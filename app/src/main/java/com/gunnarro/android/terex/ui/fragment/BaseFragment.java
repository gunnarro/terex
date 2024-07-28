package com.gunnarro.android.terex.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
     * setup after view is successfully created
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(view);
    }

    /**
     * show on top or bottom
     */
    protected void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView().getRootView(), msg, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(bgColor, theme));

        // custom snackbar layout
        // Inflate our custom view

        View snackView = getLayoutInflater().inflate(R.layout.custom_snackbar_layout, null);
        // Configure the view
        ImageView imageView = snackView.findViewById(R.id.snackbar_icon);
        //imageView.setImageBitmap(image);
        TextView messageView = snackView.findViewById(R.id.snackbar_message);
        messageView.setText(msg);
        //messageView.setTextColor(bgColor);
/*
        View snackBarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        snackBarView.setLayoutParams(params);
*/
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

    /*
     * The navigate() pushes the given toFragmentId to the top of the stack.
     * Which cause that that will be the destination fragment when clicking th back button.
     */
    protected void navigateTo(int toFragmentId, Bundle bundle) {
        navController.navigate(toFragmentId, bundle);
        // do not save any navigation history, tyr to force to follow the navigation graph, i.e, always return to parent view.
        navController.clearBackStack(toFragmentId);
    }

    protected void navigateTo(int toFragmentId, Bundle bundle, boolean isDirect) {
        if (isDirect) {
            boolean b = navController.popBackStack(toFragmentId, false);
            Log.d("navigateTo", "navigate directly, success=" + b);
        } else {
            navigateTo(toFragmentId, bundle);
        }
    }

    // -------------------------------------
    // common data input validation
    // -------------------------------------

    /**
     * used to check if a input field is empty or not
     *
     * @param e input field to check
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
