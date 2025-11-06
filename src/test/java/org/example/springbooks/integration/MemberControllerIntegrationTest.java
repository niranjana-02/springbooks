package org.example.springbooks.integration;

import org.example.springbooks.SpringbooksApplication;
import org.example.springbooks.model.Member;
import org.example.springbooks.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringbooksApplication.class)
@AutoConfigureMockMvc
@Transactional
class MemberControllerIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;

    private final String jwt = "valid-token"; // accepted by SecurityConfig

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
    }

    @Test
    void fullMemberCrudFlow_shouldWorkCorrectly() throws Exception {
        // ---------- CREATE ----------
        String createBody = """
                {
                    "name": "Niranjana",
                    "email": "niranjana@example.com"
                }
                """;

        mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Niranjana"))
                .andExpect(jsonPath("$.email").value("niranjana@example.com"));

        Member saved = memberRepository.findAll().get(0);
        Long memberId = saved.getId();
        assertThat(memberId).isNotNull();

        // ---------- GET ----------
        mvc.perform(get("/api/members/{id}", memberId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Niranjana"))
                .andExpect(jsonPath("$.email").value("niranjana@example.com"));

        // ---------- LIST ----------
        mvc.perform(get("/api/members?page=0&size=5")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Niranjana"));

        // ---------- UPDATE ----------
        String updateBody = """
                {
                    "name": "Niranjana Rajan",
                    "email": "niranjana.rajan@example.com"
                }
                """;

        mvc.perform(put("/api/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Niranjana Rajan"))
                .andExpect(jsonPath("$.email").value("niranjana.rajan@example.com"));

        Member updated = memberRepository.findById(memberId).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Niranjana Rajan");
        assertThat(updated.getEmail()).isEqualTo("niranjana.rajan@example.com");
    }

    @Test
    void getMember_notFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/members/9999")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMembers_unauthorized_shouldReturn401() throws Exception {
        mvc.perform(get("/api/members"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createMember_invalidEmail_shouldReturn400() throws Exception {
        String invalidBody = """
                {
                    "name": "John Doe",
                    "email": "invalid-email"
                }
                """;

        mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest());
    }
}