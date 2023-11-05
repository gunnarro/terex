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

    /**
     * SELECT start_date,strftime('%Y',start_date) as "Year",
     * strftime('%m',start_date) as "Month",
     * strftime('%d',start_date) as "Day"
     * FROM job_history;
     */
    @Query("SELECT * FROM timesheetEntry WHERE id = :timesheetId ORDER BY workdayDate ASC")
    List<TimesheetEntry> getTimesheetEntryList(Long timesheetId);

    @Query("SELECT * FROM timesheet_entry WHERE timesheet_id = :timesheetId ORDER BY workday_date ASC")
    LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId);

    @Query("SELECT * FROM timesheet_entry WHERE strftime('%Y', workday_date) = :week")
    LiveData<List<TimesheetEntry>> getByWeek(int week);

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
     */
    @Delete
    void delete(TimesheetEntry timesheetEntry);

}
