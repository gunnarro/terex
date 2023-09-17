package com.gunnarro.android.terex.repository;

import android.app.Application;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.RecruitmentCompany;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecruitmentRepository {

    private RecruitmentDao recruitmentDao;

    @Inject
    public RecruitmentRepository(Application application) {
       // recruitmentDao = AppDatabase.getDatabase(application).recruitmentDao();
    }

    public List<String> getAllRecruitmentCompanyNames() {
        //return recruitmentDao.getAllRecruitmentCompanies();
        return List.of("Norwegian Consulting AS", "E-Work");
    }

    public List<String> getAllProjects(Integer recruitmentCompanyId) {
        //return recruitmentDao.getAllRecruitmentCompanies();
        if (recruitmentCompanyId == 1)
            return List.of("CatalystOne Solution AS");
        else
            return List.of("Ruter");
    }


}
