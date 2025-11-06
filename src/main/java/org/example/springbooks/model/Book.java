package org.example.springbooks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books", uniqueConstraints = {@UniqueConstraint(columnNames = {"isbn"})})
@Schema(description = "Represents a book in the library")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated book id", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Schema(description = "Title of the book", example = "Clean Code")
    private String title;

    @NotBlank
    @Schema(description = "Author of the book", example = "Robert C. Martin")
    private String author;

    @NotBlank
    @Schema(description = "ISBN identifier", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Whether the book is currently borrowed", example = "false")
    private boolean isBorrowed = false;
}