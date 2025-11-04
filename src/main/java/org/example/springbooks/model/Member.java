package org.example.springbooks.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@Schema(description = "Represents a library member entity")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the member", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Schema(description = "Full name of the member", example = "Niranjana Rajan")
    private String name;

    @Email
    @NotBlank
    @Schema(description = "Unique email address of the member", example = "niranjana@example.com")
    private String email;
}
