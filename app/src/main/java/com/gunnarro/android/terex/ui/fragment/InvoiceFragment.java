package com.gunnarro.android.terex.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceFragment extends Fragment {

    @Inject
    public InvoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        /*
        view.findViewById(R.id.btn_invoice_save_as_pdf).setOnClickListener(v -> {
            try {
                toPdf();
                Toast.makeText(MainActivity.this, R.string.clicked_on_me, Toast.LENGTH_LONG).show();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        */
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // updateInvoiceView(requireView());
        // new LoadDataTask().execute(0);
        updateInvoiceView(view);
        addInvoiceLines(view);
        //   getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateInvoiceView(View view) {
        ((TextView) view.findViewById(R.id.invoice_company_name)).setText("gunnarro as");
        ((TextView) view.findViewById(R.id.invoice_company_business_address)).setText("Stavangergata 35, 0467 OSLO");
        ((TextView) view.findViewById(R.id.invoice_company_organization_number)).setText("828 707 922");
        ((TextView) view.findViewById(R.id.invoice_company_bank_account_number)).setText("9230 26 98831");
        ((TextView) view.findViewById(R.id.invoice_client_name)).setText("Technogarden AS");
        ((TextView) view.findViewById(R.id.invoice_client_address)).setText("Vestfjordgaten 4, 1338 Sandvika");
        ((TextView) view.findViewById(R.id.invoice_client_phone_number)).setText("67 57 10 00");
        ((TextView) view.findViewById(R.id.invoice_client_organization_number)).setText("986 076 905");
        ((TextView) view.findViewById(R.id.invoice_client_project_code)).setText("20-2-8879 - Javautvikler i Mastercard - 103366");
        ((TextView) view.findViewById(R.id.invoice_client_contact_person)).setText("Marcus Andersen");
        Log.d(Utility.buildTag(getClass(), "saveRegisterWork"), String.format("updated %s ", ""));
    }

    private void toPdf() throws Exception {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(getView().getWidth(), getView().getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        getView().draw(page.getCanvas());

        document.finishPage(page);
        String path = getActivity().getFilesDir().getPath() + "/invoice.pdf";
        Log.d(Utility.buildTag(getClass(), "toPdf"), "start..." + path);
        document.writeTo(new FileOutputStream(path));
        document.close();
    }

    private void addInvoiceLines(View view) {
        TableLayout tableLayout = view.findViewById(R.id.invoice_lines_tbl);
        //tableLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        // tableLayout.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorNumbers));

        tableLayout.addView(createTableRow(tableLayout.getContext(), "5", "01.03 - 31.03.2022", "29.00", "34567,00"));//,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableLayout.addView(createTableRow(tableLayout.getContext(), "6", "01.03 - 31.03.2022", "37.50", "40000,00"));//, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

       // tableLayout.requestLayout();
        Log.d(Utility.buildTag(getClass(), "addInvoiceLnes"), "updated invoice table, children: " + tableLayout.getChildCount());
    }

    private TableRow createTableRow(Context context, String week, String date, String hours, String amount) {
        TableRow row = new TableRow(context);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(rowParams);

        row.addView(createTextView(context, week));
        row.addView(createTextView(context, date));
        row.addView(createTextView(context, hours));
        row.addView(createTextView(context, amount));
        return row;
    }

    /**
     * android:layout_width="0dp"
     * android:layout_height="wrap_content"
     * android:layout_weight=".60"
     * android:gravity="center"
     * android:padding="1dip"
     * android:maxLength="3"
     * android:inputType="number"
     * android:text="5"/>
     *
     * @param text
     * @return
     */
    private TextView createTextView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(1, 1, 1, 1);
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.BLACK);
        textView.setText(text);
        return textView;
    }


    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
            //addInvoiceLnes();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
