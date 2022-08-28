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
import com.gunnarro.android.terex.domain.entity.Timesheet;

import java.time.LocalDate;
import java.util.List;

@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class})
@Dao
public interface TimesheetDao {

    @Query("SELECT * FROM timesheet WHERE id = :id")
    Timesheet getById(long id);

    @Query("SELECT * FROM timesheet WHERE client_name = :clientName AND  project_name = :projectName AND workday_date = :workdayDate")
    Timesheet getTimesheet(String clientName, String projectName, LocalDate workdayDate);

    /**
     * SELECT start_date,strftime('%Y',start_date) as "Year",
     * strftime('%m',start_date) as "Month",
     * strftime('%d',start_date) as "Day"
     * FROM job_history;
     */
    @Query("SELECT * FROM timesheet WHERE client_name = :clientName AND strftime('%m', datetime(workday_date, 'unixepoch')) = :monthNumber")
    List<Timesheet> getTimesheetByMonth(String clientName, Integer monthNumber);

    @Query("SELECT * FROM timesheet WHERE client_name = :clientName AND strftime('%d', workday_date) = :monthNumber")
    List<Timesheet> getTimesheetByWeek(String clientName, Integer monthNumber);


    @Query("SELECT * FROM timesheet ORDER BY workday_date ASC")
    LiveData<List<Timesheet>> getAll();

    @Query("SELECT * FROM timesheet WHERE strftime('%Y', workday_date) = :week")
    LiveData<List<Timesheet>> getByWeek(int week);

    /**
     *
     * @param timesheet timesheet to be inserted
     * @return the id of the inserted timesheet row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(Timesheet timesheet);

    /**
     *
     * @param timesheet updated timesheet
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Timesheet timesheet);

    /**
     *
     * @param timesheet
     */
    @Delete
    void delete(Timesheet timesheet);

}
