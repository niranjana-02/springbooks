package org.example.springbooks.service;

import org.example.springbooks.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    /**
     * Create a new book record.
     */
    Book create(Book b);

    /**
     * List all books with pagination support.
     */
    Page<Book> list(Pageable pageable);

    /**
     * Retrieve details of a specific book by ID.
     */
    Book get(Long id);

    /**
     * Update an existing book.
     */
    Book update(Long id, Book updated);

    /**
     * Delete a book by ID.
     */
    void delete(Long id);

    /**
     * Borrow a book by a member (implements business rules).
     */
    void borrow(Long bookId, Long memberId);

    /**
     * Return a borrowed book (implements business rules).
     */
    void returnBook(Long bookId);
}