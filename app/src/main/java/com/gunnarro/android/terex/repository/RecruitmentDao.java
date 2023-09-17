package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.RecruitmentCompany;

import java.util.List;
@Dao
public interface RecruitmentDao {

    @Query("SELECT * FROM recruitment_company WHERE id = :id")
    RecruitmentCompany getById(long id);

    @Query("SELECT * FROM recruitment_company ORDER BY id DESC")
    List<RecruitmentCompany> getAllRecruitmentCompanies();

    @Query("SELECT r.company_name FROM recruitment_company r ORDER BY r.company_name DESC")
    List<String> getAllRecruitmentCompanyNames();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Long insert(RecruitmentCompany recruitmentCompany);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(RecruitmentCompany recruitmentCompany);


    @Delete
    void delete(RecruitmentCompany recruitmentCompany);

}
