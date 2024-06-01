package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetSummaryFragment extends BaseFragment {

    private TimesheetService timesheetService;
    private ProjectService projectService;

    private Long timesheetId;

    @Inject
    public TimesheetSummaryFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_timesheet_summary);
        timesheetService = new TimesheetService();
        projectService = new ProjectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_summary, container, false);
        setHasOptionsMenu(true);
        timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildTimesheetSummary(timesheetId);
    }

    private void buildTimesheetSummary(Long timesheetId) {
        Integer hourlyRate = projectService.getProjectHourlyRate(timesheetId);
        String timesheetSummaryHtml = timesheetService.createTimesheetSummaryHtml(timesheetId, hourlyRate, requireContext());
        WebView webView;
        try {
            webView = requireView().findViewById(R.id.timesheet_summary_web_view);
        } catch (Exception e) {
            // ignore, view is not ready yet
            Log.e("loadInvoiceAttachment", e.getMessage());
            return;
        }

        Log.d("", "html=" + timesheetSummaryHtml);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        //    webView.setWebViewClient(new LocalContentWebViewClient());
        //webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
        Log.d("timesheet summary", String.format("%s", timesheetSummaryHtml));
        webView.loadDataWithBaseURL("file:///android_asset/", timesheetSummaryHtml, "text/html", "UTF-8", null);
    }

}
