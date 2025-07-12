package com.personal.kopmorning.report.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("관리자 권한 없으면 접근 차단")
    @WithMockUser(username = "user", roles = {"USER"})
        // 일반 사용자
    void getList_shouldDenyAccessForUser() throws Exception {
        mockMvc.perform(get("/admin/report/list")) // 실제 endpoint
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 권한으로 전체 신고 리스트 조회")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
        // 관리자 권한
    void getList_shouldReturnReportList() throws Exception {
        mockMvc.perform(get("/admin/report/list"))
                .andExpect(status().isOk());
    }
}