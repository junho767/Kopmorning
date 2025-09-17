package com.personal.kopmorning.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.kopmorning.domain.admin.dto.request.RollUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RedissonClient redissonClient() {
            return Mockito.mock(RedissonClient.class);
        }
    }

    @Nested
    @DisplayName("ADMIN 권한 접근 허용 테스트")
    class AdminAccess {

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void memberList_ok() throws Exception {
            mockMvc.perform(get("/admin/member/list"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void articleList_ok() throws Exception {
            mockMvc.perform(get("/admin/article/list/football"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        void updateRole_ok() throws Exception {
            RollUpdateRequest dto = new RollUpdateRequest(1L, "ADMIN");

            mockMvc.perform(patch("/admin/roll")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }
}


