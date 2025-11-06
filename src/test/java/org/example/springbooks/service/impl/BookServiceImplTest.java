package org.example.springbooks.service.impl;

import org.example.springbooks.exception.InvalidOperationException;
import org.example.springbooks.exception.ResourceNotFoundException;
import org.example.springbooks.model.Book;
import org.example.springbooks.model.Member;
import org.example.springbooks.repository.BookRepository;
import org.example.springbooks.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock BookRepository bookRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks BookServiceImpl bookService;

    // ---------- create ----------
    @Test
    void create_shouldSaveAndReturnBook() {
        Book input = Book.builder().title("Clean Code").author("Uncle Bob").isbn("978-0132350884").build();
        Book saved = Book.builder().id(1L).title("Clean Code").author("Uncle Bob").isbn("978-0132350884").isBorrowed(false).build();

        when(bookRepository.save(input)).thenReturn(saved);

        Book result = bookService.create(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepository, times(1)).save(input);
    }

    // ---------- list ----------
    @Test
    void list_shouldReturnPagedBooks() {
        Book b1 = Book.builder().id(1L).title("A").author("Auth A").isbn("ISBN-A").build();
        Book b2 = Book.builder().id(2L).title("B").author("Auth B").isbn("ISBN-B").build();
        Page<Book> page = new PageImpl<>(Arrays.asList(b1, b2));

        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<Book> result = bookService.list(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("A", result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void list_empty_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(1, 10);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Book> result = bookService.list(pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    // ---------- get ----------
    @Test
    void get_existing_shouldReturnBook() {
        Book b = Book.builder().id(42L).title("X").author("Y").isbn("Z").build();
        when(bookRepository.findById(42L)).thenReturn(Optional.of(b));

        Book result = bookService.get(42L);

        assertEquals(42L, result.getId());
        verify(bookRepository, times(1)).findById(42L);
    }

    @Test
    void get_nonexistent_shouldThrow() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.get(99L));
        verify(bookRepository, times(1)).findById(99L);
    }

    // ---------- update ----------
    @Test
    void update_existing_shouldCopyFieldsAndSave() {
        Book existing = Book.builder().id(5L).title("Old").author("OldA").isbn("OLD-ISBN").isBorrowed(false).build();
        Book updated  = Book.builder().title("New").author("NewA").isbn("NEW-ISBN").build();

        when(bookRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = bookService.update(5L, updated);

        assertEquals("New", result.getTitle());
        assertEquals("NewA", result.getAuthor());
        assertEquals("NEW-ISBN", result.getIsbn());
        verify(bookRepository, times(1)).save(existing);
    }

    @Test
    void update_nonexistent_shouldThrow() {
        when(bookRepository.findById(5L)).thenReturn(Optional.empty());
        Book updated  = Book.builder().title("New").author("NewA").isbn("NEW-ISBN").build();

        assertThrows(ResourceNotFoundException.class, () -> bookService.update(5L, updated));
        verify(bookRepository, times(1)).findById(5L);
        verify(bookRepository, never()).save(any());
    }

    // ---------- delete ----------
    @Test
    void delete_existing_shouldDelete() {
        Book existing = Book.builder().id(10L).title("Keep it").author("Me").isbn("K-1").build();
        when(bookRepository.findById(10L)).thenReturn(Optional.of(existing));

        bookService.delete(10L);

        verify(bookRepository, times(1)).delete(existing);
    }

    @Test
    void delete_nonexistent_shouldThrow() {
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(10L));
        verify(bookRepository, times(1)).findById(10L);
        verify(bookRepository, never()).delete(any());
    }

    // ---------- borrow ----------
    @Test
    void borrow_nonexistentBook_shouldThrow() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.borrow(1L, 2L));
        verify(bookRepository, times(1)).findById(1L);
        verify(memberRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrow_nonexistentMember_shouldThrow() {
        Book b = Book.builder().id(1L).title("T").author("A").isbn("I").isBorrowed(false).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.borrow(1L, 2L));
        verify(memberRepository, times(1)).findById(2L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrow_alreadyBorrowed_shouldThrow() {
        Book b = Book.builder().id(1L).title("T").author("A").isbn("I").isBorrowed(true).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(Member.builder().id(2L).name("M").email("m@x.com").build()));

        assertThrows(InvalidOperationException.class, () -> bookService.borrow(1L, 2L));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrow_success_shouldMarkBorrowedAndSave() {
        Book b = Book.builder().id(1L).title("T").author("A").isbn("I").isBorrowed(false).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(Member.builder().id(2L).name("M").email("m@x.com").build()));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.borrow(1L, 2L);

        assertTrue(b.isBorrowed());
        verify(bookRepository, times(1)).save(b);
    }

    // ---------- returnBook ----------
    @Test
    void returnBook_nonexistent_shouldThrow() {
        when(bookRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.returnBook(7L));
        verify(bookRepository, times(1)).findById(7L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_notBorrowed_shouldThrow() {
        Book b = Book.builder().id(7L).title("T").author("A").isbn("I").isBorrowed(false).build();
        when(bookRepository.findById(7L)).thenReturn(Optional.of(b));

        assertThrows(InvalidOperationException.class, () -> bookService.returnBook(7L));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_success_shouldUnsetBorrowedAndSave() {
        Book b = Book.builder().id(7L).title("T").author("A").isbn("I").isBorrowed(true).build();
        when(bookRepository.findById(7L)).thenReturn(Optional.of(b));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.returnBook(7L);

        assertFalse(b.isBorrowed());
        verify(bookRepository, times(1)).save(b);
    }
}
