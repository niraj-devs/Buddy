package com.buddy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfigTest.ProtectedEndpointConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpointShouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/test/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointShouldRejectInvalidJwt() throws Exception {
        mockMvc.perform(get("/test/protected")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointShouldAcceptAuthenticatedTestRequest() throws Exception {
        mockMvc.perform(get("/test/protected")
                        .with(user("42")))
                .andExpect(status().isOk());
    }

    @Test
    void adminOpportunityEndpointShouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(post("/api/admin/opportunities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminOpportunityEndpointShouldRejectStudentRole() throws Exception {
        mockMvc.perform(post("/api/admin/opportunities")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminOpportunityEndpointShouldAllowAdminRolePastAuthorization() throws Exception {
        mockMvc.perform(post("/api/admin/opportunities")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminStudentsEndpointShouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminStudentsEndpointShouldRejectStudentRole() throws Exception {
        mockMvc.perform(get("/api/admin/students")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminStudentsEndpointShouldAllowAdminRolePastAuthorization() throws Exception {
        mockMvc.perform(get("/api/admin/students")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class ProtectedEndpointConfig {

        @Bean
        ProtectedTestController protectedTestController() {
            return new ProtectedTestController();
        }
    }

    @RestController
    static class ProtectedTestController {

        @GetMapping("/test/protected")
        String protectedEndpoint() {
            return "authenticated";
        }
    }
}
