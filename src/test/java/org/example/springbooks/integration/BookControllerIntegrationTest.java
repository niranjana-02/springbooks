package org.example.springbooks.integration;

import org.example.springbooks.SpringbooksApplication;
import org.example.springbooks.model.Book;
import org.example.springbooks.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringbooksApplication.class)
@AutoConfigureMockMvc
class BookControllerIntegrationTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
    }

    @Test
    void createAndGetBook_flow() throws Exception {
        String jwt = "test-token";
        String body = "{\"title\":\"Book A\",\"author\":\"Author\",\"isbn\":\"ISBN-1\"}";
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(body).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mvc.perform(get("/api/books").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
