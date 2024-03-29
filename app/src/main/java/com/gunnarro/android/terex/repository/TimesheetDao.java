package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;

import java.util.List;
import java.util.Map;

@Dao
public interface TimesheetDao {

    @Query("SELECT * FROM timesheet t JOIN timesheet_entry e ON t.id = e.timesheet_id WHERE t.id = :timesheetId")
    LiveData<Map<Timesheet, List<TimesheetEntry>>> getTimesheetLiveData(Long timesheetId);

    @Query("SELECT * FROM timesheet WHERE id = :id")
    Timesheet getTimesheetById(Long id);

    /**
     * use transactions since this method return a aggregate object
     */
    @Transaction
    @Query("SELECT * FROM timesheet WHERE id = :timesheetId")
    TimesheetWithEntries getTimesheetWithEntries(Long timesheetId);

    @Query("SELECT * FROM timesheet WHERE client_name = :clientName AND project_code = :projectCode AND year = :year AND month = :month")
    Timesheet getTimesheet(String clientName, String projectCode, Integer year, Integer month);

    @Query("SELECT * FROM timesheet where year = :year ORDER BY client_name")
   // @Query("SELECT t.*, count(e.timesheet_id) AS registeredWorkingDays , sum(e.worked_in_min) as  registeredWorkingHours FROM timesheet t"
   //         + " INNER JOIN  timesheet_entry e ON e.timesheet_id = t.id "
   //         + " WHERE year = :year")
    LiveData<List<Timesheet>> getTimesheetByYear(Integer year);

    @Query("SELECT * FROM timesheet WHERE status = :status ORDER BY year, month DESC")
    List<Timesheet> getTimesheets(String status);

    /**
     * @param timesheet timesheet to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted timesheet entry row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(Timesheet timesheet);

    /**
     * @param timesheet updated timesheet. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Timesheet timesheet);

    /**
     * @param timesheet to be deleted
     */
    @Delete
    void delete(Timesheet timesheet);

}
