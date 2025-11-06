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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringbooksApplication.class)
@AutoConfigureMockMvc
@Transactional
class BookControllerIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    BookRepository bookRepository;

    private final String jwt = "valid-token";

    @BeforeEach
    void cleanDB() {
        bookRepository.deleteAll();
    }

    @Test
    void fullBookCrudFlow_shouldWorkCorrectly() throws Exception {
        // ---------- CREATE ----------
        String createBody = """
                {
                    "title": "Book A",
                    "author": "Author One",
                    "isbn": "ISBN-123"
                }
                """;

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Book A"))
                .andReturn().getResponse().getContentAsString();

        // Extract the created book ID
        Long bookId = bookRepository.findAll().get(0).getId();
        assertThat(bookId).isNotNull();

        // ---------- GET ----------
        mvc.perform(get("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book A"))
                .andExpect(jsonPath("$.author").value("Author One"));

        // ---------- LIST ----------
        mvc.perform(get("/api/books?page=0&size=10")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].isbn").value("ISBN-123"));

        // ---------- UPDATE ----------
        String updateBody = """
                {
                    "title": "Book A - Updated",
                    "author": "Author Two",
                    "isbn": "ISBN-999"
                }
                """;

        mvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book A - Updated"))
                .andExpect(jsonPath("$.author").value("Author Two"))
                .andExpect(jsonPath("$.isbn").value("ISBN-999"));

        Book updated = bookRepository.findById(bookId).orElseThrow();
        assertThat(updated.getAuthor()).isEqualTo("Author Two");

        // ---------- DELETE ----------
        mvc.perform(delete("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertThat(bookRepository.existsById(bookId)).isFalse();
    }

    @Test
    void getAllBooks_unauthorized_shouldReturn401() throws Exception {
        // No token
        mvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBook_notFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/books/9999")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }
}