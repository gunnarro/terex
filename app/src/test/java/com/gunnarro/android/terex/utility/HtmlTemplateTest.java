package com.gunnarro.android.terex.utility;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HtmlTemplateTest {

    @Test
    void htmlTemplate() throws FileNotFoundException {
        FileReader mustacheTemplateFile = new FileReader("src/test/timesheet-attachment.mustache");
        String resourceRoot = "";
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile(mustacheTemplateFile,"");
        List<TimesheetSummary> invoiceSummaryList = new ArrayList<>();
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceSummaryList", invoiceSummaryList);
        context.put("sunBilledHours", "192,5");
        context.put("sumBilledAmount", "194567,00");
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        System.out.println(writer.toString());

    }
}
