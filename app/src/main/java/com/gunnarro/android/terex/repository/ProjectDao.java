package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    @Query("select * from project")
    List<Project> getAllProjects();

    @Update
    void updateProject(Project project);

    @Delete
    void deleteProject(Project project);

}
