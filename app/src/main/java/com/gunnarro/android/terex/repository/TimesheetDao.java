package com.gunnarro.android.terex.repository;

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

@Dao
public interface TimesheetDao {

    @Query("SELECT * FROM timesheet WHERE id = :id")
    Timesheet getTimesheetById(Long id);

    @Transaction
    @Query("SELECT * FROM timesheet WHERE id = :id")
    TimesheetWithEntries getTimesheetWithEntriesById(Long id);

    @Query("SELECT * FROM timesheet WHERE client_name = :clientName AND project_code = :projectCode AND year = :year AND month = :month")
    Timesheet getTimesheet(String clientName, String projectCode, Integer year, Integer month);

    @Query("SELECT * FROM timesheet ORDER BY client_name")
    List<Timesheet> getAllTimesheets();

    /**
     * @param timesheet timesheet to be inserted
     * @return the id of the inserted timesheet entry row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(Timesheet timesheet);

    /**
     * @param timesheet updated timesheet
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.ABORT)
    int update(Timesheet timesheet);

    /**
     * @param timesheet to be deleted
     */
    @Delete
    void delete(Timesheet timesheet);


}
