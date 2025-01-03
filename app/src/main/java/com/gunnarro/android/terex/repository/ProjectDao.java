package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM project WHERE id = :projectId")
    Project getProject(Long projectId);

    @Query("select * from project where client_id = :clientId and status IN(:statuses) order by name")
    List<Project> getProjects(Long clientId, List<String> statuses);

    @Query("select * from project where client_id = :clientId and name = :projectName order by name")
    Project findProject(Long clientId, String projectName);

    @Query("SELECT client.name FROM project"
            + " INNER JOIN client ON client.id = project.client_id"
            + " WHERE project.id = :projectId")
    String getClientName(Long projectId);

    /**
     * Get hourly rate for timesheet id
     *
     * @param timesheetId timesheet id
     * @return hourly rate
     */
    @Query("SELECT project.hourly_rate FROM project"
            + " INNER JOIN timesheet ON timesheet.client_id = project.id"
            + " WHERE timesheet.id = :timesheetId")
    Integer getProjectHourlyRate(Long timesheetId);

    /**
     * @param project project to be inserted. Abort if conflict, i.e. silently drop the insert.
     * @return the id of the inserted invoice row
     */
    @Insert
    long insert(Project project);

    /**
     * @param project updated project. Replace on conflict, i.e, replace old data with the new one.
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Project project);

    /**
     * @param project to be deleted
     */
    @Delete
    void delete(Project project);
}
