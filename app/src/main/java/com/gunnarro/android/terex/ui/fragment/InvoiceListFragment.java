package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.MainActivity;
import com.gunnarro.android.terex.ui.adapter.InvoiceListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.swipe.SwipeCallback;
import com.gunnarro.android.terex.ui.view.InvoiceViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String INVOICE_REQUEST_KEY = "invoice_request";
    public static final String INVOICE_ID_KEY = "invoice_id";
    public static final String INVOICE_ACTION_KEY = "invoice_action";
    public static final String INVOICE_ACTION_VIEW = "invoice_view";
    private InvoiceViewModel invoiceViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // displays the back button on toolbar
        ((MainActivity) requireActivity()).showUpButton();
        // Get a new or existing ViewModel from the ViewModelProvider.
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        getParentFragmentManager().setFragmentResultListener(INVOICE_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "requestKey: " + requestKey + ", bundle: " + bundle);
                handleFragmentResult(bundle);
            }
        });
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_invoices);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_invoice_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.invoice_list_recyclerview);
        final InvoiceListAdapter adapter = new InvoiceListAdapter(new InvoiceListAdapter.InvoiceDtoDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        invoiceViewModel.getAllInvoices().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.invoice_list_add_btn);
        addButton.setOnClickListener(v -> navigateTo(R.id.nav_from_invoice_list_to_invoice_new, null));

        enableSwipeToLeftAndDeleteItem(recyclerView);
        enableSwipeToRightAndViewItem(recyclerView);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getLong(INVOICE_ID_KEY) > 0) {
            navigateTo(R.id.nav_from_invoice_list_to_invoice_details, bundle);
        }
    }

    /**
     * Open invoice details when clicked on an invoice in the list.
     */
    @Override
    public void onItemClick(Bundle bundle) {
        navigateTo(R.id.nav_from_invoice_list_to_invoice_details, bundle);
    }

    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToViewCallback = new SwipeCallback(requireContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedProjectPos = viewHolder.getAbsoluteAdapterPosition();
                InvoiceDto invoiceDto = invoiceViewModel.getAllInvoices().getValue().get(selectedProjectPos);
                Bundle bundle = new Bundle();
                bundle.putLong(INVOICE_ID_KEY, invoiceDto.getId());
                navigateTo(R.id.invoice_details_fragment, bundle);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToViewCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for invoice list item");
    }

    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedInvoicePos = viewHolder.getAbsoluteAdapterPosition();
                InvoiceDto invoiceDto = invoiceViewModel.getAllInvoices().getValue().get(selectedInvoicePos);
                confirmDeleteInvoiceDialog(getString(R.string.msg_delete_invoice), getString(R.string.msg_confirm_delete), invoiceDto.getId());
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete invoice list item");
    }

    private void deleteInvoice(Long invoiceId) {
        try {
            invoiceViewModel.deleteInvoice(invoiceId);
            // in order to wipe out the swipe item color, must be a better way to do this.
            navigateTo(R.id.nav_to_invoice_list, null);
            showSnackbar(String.format(getResources().getString(R.string.info_list_delete_msg_format), "Invoice"), R.color.color_snackbar_text_delete);
        } catch (TerexApplicationException | InputValidationException e) {
            showInfoDialog("Info", e.getMessage());
            navigateTo(R.id.nav_to_invoice_list, null);
        }
    }

    private void confirmDeleteInvoiceDialog(final String title, final String message, final Long invoiceId) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> deleteInvoice(invoiceId))
                .setNeutralButton(R.string.btn_cancel, (dialogInterface, i) -> {
                    // in order to wipe out the swipe item color, must be a better way to do this.
                    navigateTo(R.id.nav_to_invoice_list, null);
                })
                .show();
    }

}
