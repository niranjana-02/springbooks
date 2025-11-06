package org.example.springbooks.service.impl;

import org.example.springbooks.model.Book;
import org.example.springbooks.model.Member;
import org.example.springbooks.repository.BookRepository;
import org.example.springbooks.exception.ResourceNotFoundException;
import org.example.springbooks.exception.InvalidOperationException;
import org.example.springbooks.repository.MemberRepository;
import org.example.springbooks.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public BookServiceImpl(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public Book create(Book b) {
        return bookRepository.save(b);
    }

    public Page<Book> list(Pageable p) {
        return bookRepository.findAll(p);
    }

    public Book get(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }

    public Book update(Long id, Book updated) {
        Book b = get(id);
        b.setAuthor(updated.getAuthor());
        b.setTitle(updated.getTitle());
        b.setIsbn(updated.getIsbn());
        return bookRepository.save(b);
    }

    public void delete(Long id) {
        bookRepository.delete(get(id));
    }

    /**
     * Borrow a book by a member following all business rules.
     */
    public void borrow(Long bookId, Long memberId) {
        // Rule 3: Book must exist
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        // Rule 2: Member must exist
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        // Rule 1: Book must not already be borrowed
        if (book.isBorrowed()) {
            throw new InvalidOperationException("Book '" + book.getTitle() + "' is already borrowed.");
        }

        // Mark as borrowed and save
        book.setBorrowed(true);
        bookRepository.save(book);
    }

    /**
     * Return a borrowed book following all business rules.
     */
    public void returnBook(Long bookId) {
        // Rule 2: Book must exist
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        // Rule 1: Book must currently be borrowed
        if (!book.isBorrowed()) {
            throw new InvalidOperationException("Book '" + book.getTitle() + "' is not currently borrowed.");
        }

        // Mark as returned and save
        book.setBorrowed(false);
        bookRepository.save(book);
    }
}
