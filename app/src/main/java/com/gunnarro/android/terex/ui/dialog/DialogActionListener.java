package com.gunnarro.android.terex.ui.dialog;

public interface DialogActionListener {
    int OK_ACTION = 1;
    int CANCEL_ACTION = 0;

    void onDialogAction(int actionCode, Long entityId);
}
