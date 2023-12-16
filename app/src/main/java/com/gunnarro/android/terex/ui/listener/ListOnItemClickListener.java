package com.gunnarro.android.terex.ui.listener;

import android.os.Bundle;

/**
 * Custom list item on-click listener.
 * Use to direct the item click event back to the fragment., so the fragment itself can deal with the business logic.
 */
public interface ListOnItemClickListener {

    void onItemClick(Bundle bundle);
}
