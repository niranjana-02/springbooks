package org.example.springbooks.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.springbooks.model.Book;
import org.example.springbooks.service.impl.BookServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Books", description = "APIs for managing books")
public class BookController {
    private final BookServiceImpl bookServiceImpl;

    public BookController(BookServiceImpl bookServiceImpl) {
        this.bookServiceImpl = bookServiceImpl;
    }

    @Operation(summary = "Create a new book", description = "Create and persist a new Book")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Book to create",
            required = true,
            content = @Content(schema = @Schema(implementation = Book.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(implementation = Book.class)))
    })
    @PostMapping("/books")
    public ResponseEntity<Book> create(@Valid @RequestBody Book b) {
        return ResponseEntity.ok(bookServiceImpl.create(b));
    }

    @Operation(summary = "List books", description = "Returns a paginated list of books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    })
    @GetMapping("/books")
    public ResponseEntity<Page<Book>> list(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookServiceImpl.list(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get book by id", description = "Retrieve a single book by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found", content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> get(@Parameter(description = "Book id", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(bookServiceImpl.get(id));
    }

    @Operation(summary = "Update a book", description = "Update an existing book by id")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated book payload",
            required = true,
            content = @Content(schema = @Schema(implementation = Book.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> update(@Parameter(description = "Book id", required = true) @PathVariable("id") Long id,
                                       @Valid @RequestBody Book b) {
        return ResponseEntity.ok(bookServiceImpl.update(id, b));
    }

    @Operation(summary = "Delete a book", description = "Delete a book by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Book id", required = true) @PathVariable("id") Long id) {
        bookServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borrow/{bookId}/member/{memberId}")
    public ResponseEntity<String> borrow(@PathVariable Long bookId, @PathVariable("memberId") Long memberId) {
        bookServiceImpl.borrow(bookId, memberId);
        return ResponseEntity.ok("Book borrowed");
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable("bookId") Long bookId) {
        bookServiceImpl.returnBook(bookId);
        return ResponseEntity.ok("Book returned");
    }
}