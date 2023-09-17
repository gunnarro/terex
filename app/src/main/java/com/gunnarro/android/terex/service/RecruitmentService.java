package com.gunnarro.android.terex.service;

import com.gunnarro.android.terex.domain.entity.RecruitmentCompany;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecruitmentService {

    @Inject
    public RecruitmentService() {
    }

    public void addRecruitmentCompany() {

    }

    public String[] getRecruitmentNames() {
        RecruitmentCompany recruitmentCompany = new RecruitmentCompany();
        return new String[]{"Norway Consulting AS", "Technogarden", "IT-Verket"};
    }

    public String[] getProjectNames() {
        RecruitmentCompany recruitmentCompany = new RecruitmentCompany();
        return new String[]{"catalystOne monolith", "Nets efaktura"};
    }
}
