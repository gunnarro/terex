package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.ui.adapter.InvoiceListAdapter;
import com.gunnarro.android.terex.ui.view.InvoiceViewModel;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceListFragment extends Fragment {
    public static final String INVOICE_REQUEST_KEY = "300";
    public static final String INVOICE_ID_KEY = "invoice_id";
    public static final String INVOICE_ACTION_VIEW = "invoice_view";
    private static final String INVOICE_JSON_KEY = "311";
    private InvoiceViewModel invoiceViewModel;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_invoices);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_invoice_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.invoice_list_recyclerview);
        final InvoiceListAdapter adapter = new InvoiceListAdapter(getParentFragmentManager(), new InvoiceListAdapter.InvoiceDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        invoiceViewModel.getAllInvoices().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.add_invoice);
        addButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceNewFragment.class, null)
                .setReorderingAllowed(true)
                .commit());
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
      if (bundle.getString(INVOICE_ID_KEY) != null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, InvoiceViewFragment.class, bundle)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }
}
