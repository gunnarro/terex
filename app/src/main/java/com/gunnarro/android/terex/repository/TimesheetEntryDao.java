package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import java.time.LocalDate;
import java.util.List;

@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class})
@Dao
public interface TimesheetEntryDao {

    @Query("SELECT * FROM timesheet_entry WHERE id = :id")
    TimesheetEntry getById(long id);

    @Query("SELECT * FROM timesheet_entry WHERE timesheet_id = :timesheetId ORDER BY workday_date DESC LIMIT 1")
    TimesheetEntry getMostRecent(Long timesheetId);

    @Query("SELECT * FROM timesheet_entry WHERE timesheet_id = :timesheetId AND workday_date = :workdayDate")
    TimesheetEntry getTimesheet(Long timesheetId, LocalDate workdayDate);

    @Query("SELECT count(*) FROM timesheet_entry WHERE timesheet_id = :timesheetId")
    Integer getRegisteredWorkedDays(Long timesheetId);

    // https://www.sqlitetutorial.net/sqlite-sum/
    @Query("SELECT sum(worked_in_min/60) FROM timesheet_entry WHERE timesheet_id = :timesheetId")
    Integer getRegisteredWorkedHours(Long timesheetId);

    /**
     * SELECT start_date,strftime('%Y',start_date) as "Year",
     * strftime('%m',start_date) as "Month",
     * strftime('%d',start_date) as "Day"
     * FROM job_history;
     */
    @Query("SELECT * FROM timesheet_entry WHERE timesheet_id = :timesheetId ORDER BY workday_date ASC")
    List<TimesheetEntry> getTimesheetEntryList(Long timesheetId);

    @Query("SELECT * FROM timesheet_entry WHERE timesheet_id = :timesheetId ORDER BY workday_date ASC")
    LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId);

    @Query("UPDATE timesheet_entry SET status = 'BILLED' WHERE id = :timesheetEntryId")
    int closeTimesheetEntry(Long timesheetEntryId);

    /**
     *
     * @param timesheetEntry timesheet to be inserted
     * @return the id of the inserted timesheet entry row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(TimesheetEntry timesheetEntry);

    /**
     *
     * @param timesheetEntry updated timesheet. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(TimesheetEntry timesheetEntry);

    /**
     *
     * @param timesheetEntry to be deleted
     * @return number of deleted row(S), should only be one for this method.
     */
    @Delete
    int delete(TimesheetEntry timesheetEntry);
}
