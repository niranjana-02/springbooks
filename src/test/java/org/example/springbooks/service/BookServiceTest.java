package org.example.springbooks.service;

import org.example.springbooks.model.Book;
import org.example.springbooks.repository.BookRepository;
import org.example.springbooks.exception.InvalidOperationException;
import org.example.springbooks.exception.ResourceNotFoundException;
import org.example.springbooks.repository.MemberRepository;
import org.example.springbooks.service.impl.BookServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    @Mock
    BookRepository bookRepository;
    MemberRepository memberRepository;
    BookServiceImpl bookServiceImpl;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        bookServiceImpl = new BookServiceImpl(bookRepository, memberRepository);
    }

    @Test
    void borrow_nonexistent_shouldThrow() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.borrow(1L, 1L));
    }

    @Test
    void borrow_alreadyBorrowed_shouldThrow() {
        Book b = Book.builder().id(1L).title("t").author("a").isbn("i").isBorrowed(true).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        assertThrows(InvalidOperationException.class, () -> bookServiceImpl.borrow(1L, 1L));
    }

    @Test
    void borrow_success_shouldSetFlag() {
        Book b = Book.builder().id(1L).title("t").author("a").isbn("i").isBorrowed(false).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        bookServiceImpl.borrow(1L, 2L);
        assertTrue(b.isBorrowed());
    }
}
