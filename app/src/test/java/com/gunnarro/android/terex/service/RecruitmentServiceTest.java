package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RecruitmentServiceTest {

    @Test
    void getAll() {
        RecruitmentService recruitmentService = new RecruitmentService();
        recruitmentService.addRecruitmentCompany();
        assertEquals(3, recruitmentService.getRecruitmentNames().length);
    }
}
