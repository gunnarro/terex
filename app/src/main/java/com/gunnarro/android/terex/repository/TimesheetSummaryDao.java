package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import java.util.List;

@Dao
public interface TimesheetSummaryDao {

    @Query("select * from timesheet_summary t where t.timesheet_id = :timesheetId")
    List<TimesheetSummary> getTimesheetSummaries(Long timesheetId);

    @Query("DELETE FROM timesheet_summary where timesheet_id = :timesheetId")
    void delete(Long timesheetId);

    /**
     * @param timesheetSummary timesheet to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice summary row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(TimesheetSummary timesheetSummary);

    /**
     * @param timesheetSummary updated invoice summary. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(TimesheetSummary timesheetSummary);

    /**
     * @param timesheetSummary to be deleted
     */
    @Delete
    void delete(TimesheetSummary timesheetSummary);

}
