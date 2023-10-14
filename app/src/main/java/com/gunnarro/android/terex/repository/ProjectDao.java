package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
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

    @Query("select * from project p order by company_name, project_name")
    LiveData<List<Project>> getAllProjects();

    @Query("select * from project p where p.project_status = :status order by company_name, project_name")
    List<Project> getProjects(String status);

    @Query("select * from project p where p.company_name = :companyName and p.project_name = :projectName order by company_name, project_name")
    Project findProject(String companyName, String projectName);

    @Query("SELECT * FROM project p WHERE p.id = :projectId")
    Project getProject(long projectId);

    /**
     * @param project project to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Project project);

    /**
     * @param project updated project. Replace on conflict, i.e, replace old data with the new one
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
