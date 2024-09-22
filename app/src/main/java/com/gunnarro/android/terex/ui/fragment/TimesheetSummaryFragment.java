package com.gunnarro.android.terex.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetSummaryFragment extends BaseFragment {

    private final TimesheetService timesheetService;
    private final ProjectService projectService;

    private Long timesheetId;

    @Inject
    public TimesheetSummaryFragment() {
        // Needed by dagger framework
        timesheetService = new TimesheetService();
        projectService = new ProjectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_timesheet_summary);
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
        String timesheetSummeryMustacheTemplate = loadMustacheTemplate(requireContext(), InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY);
        String timesheetSummaryHtml = timesheetService.createTimesheetSummaryHtml(timesheetId, timesheetSummeryMustacheTemplate);
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

    private String loadMustacheTemplate(Context applicationContext, InvoiceService.InvoiceAttachmentTypesEnum template) {
        StringBuilder mustacheTemplateStr = new StringBuilder();
        try (InputStream fis = applicationContext.getAssets().open(template.getTemplate()); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
            return mustacheTemplateStr.toString();
        } catch (IOException e) {
            throw new TerexApplicationException("error reading mustache template", "50050", e);
        }
    }
}
