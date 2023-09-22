package com.gunnarro.android.terex.domain.dbview;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import java.util.List;

@DatabaseView("SELECT * FROM timesheet_entry")
public class TimesheetView {
    @ColumnInfo(name = "name")
    private String name;
   // private List<String> projectNames;

    public void setName(String name) {
        this.name = name;
    }
}
