package com.gunnarro.android.terex.integration.jira;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TempoDomainMapper {

    public static final String TEMPO_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEMPO_DATE_TIME_PATTERN, Locale.getDefault());


    private TempoDomainMapper() {
    }

    public static List<TimesheetEntry> fromTempoTimesheetCvs(List<String> tempoTimesheetCsvList) {
        return tempoTimesheetCsvList.stream().map(TempoDomainMapper::fromTempoTimesheetEntryCvs
        ).collect(Collectors.toList());
    }

    public static TimesheetEntry fromTempoTimesheetEntryCvs(String timesheetEntryCvs) {
        String[] entry = timesheetEntryCvs.split(",");
        String issueName = entry[0].trim();
        String dateTimeStr = entry[1].replace("Z", "").trim();
        String workdHours = entry[2].replace("h", "").trim();
        String comments = entry[3].trim();
        LocalDateTime workDayDateTime = toLocalDateTime(dateTimeStr);
        return TimesheetEntry.create(-1L, -1L, workDayDateTime.toLocalDate(), workDayDateTime.toLocalTime(), Utility.fromHoursToSeconds(workdHours), String.format("%s, %s", issueName, comments));
    }

    public static LocalDateTime toLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.length() != TEMPO_DATE_TIME_PATTERN.length()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }
}
