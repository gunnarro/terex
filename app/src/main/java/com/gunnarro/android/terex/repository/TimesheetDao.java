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
public interface TimesheetDao {

    @Query("SELECT * FROM timesheet_entry WHERE id = :id")
    TimesheetEntry getById(long id);

    @Query("SELECT * FROM timesheet_entry ORDER BY workday_date DESC LIMIT 1")
    TimesheetEntry getMostRecent();


    @Query("SELECT * FROM timesheet_entry WHERE client_name = :clientName AND  project_code = :projectCode AND workday_date = :workdayDate")
    TimesheetEntry getTimesheet(String clientName, String projectCode, LocalDate workdayDate);

    /**
     * SELECT start_date,strftime('%Y',start_date) as "Year",
     * strftime('%m',start_date) as "Month",
     * strftime('%d',start_date) as "Day"
     * FROM job_history;
     */
    @Query("SELECT * FROM timesheet_entry WHERE client_name = :clientName AND project_code = :projectCode AND strftime('%m', datetime(workday_date, 'unixepoch')) <> :monthNumber")
    List<TimesheetEntry> getTimesheetByMonth(String clientName, String projectCode, String monthNumber);

    @Query("SELECT * FROM timesheet_entry WHERE client_name = :clientName AND strftime('%d', workday_date) = :monthNumber")
    List<TimesheetEntry> getTimesheetByWeek(String clientName, String monthNumber);

    @Query("SELECT * FROM timesheet_entry ORDER BY workday_date ASC")
    LiveData<List<TimesheetEntry>> getAll();

    @Query("SELECT * FROM timesheet_entry WHERE strftime('%Y', workday_date) = :week")
    LiveData<List<TimesheetEntry>> getByWeek(int week);

    /**
     *
     * @param timesheet timesheet to be inserted
     * @return the id of the inserted timesheet row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(TimesheetEntry timesheet);

    /**
     *
     * @param timesheet updated timesheet
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(TimesheetEntry timesheet);

    /**
     *
     * @param timesheet
     */
    @Delete
    void delete(TimesheetEntry timesheet);

}
