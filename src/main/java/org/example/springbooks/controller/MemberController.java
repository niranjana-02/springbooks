package org.example.springbooks.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.springbooks.model.Member;
import org.example.springbooks.service.impl.MemberServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/members")
@Validated
@Tag(name = "Members", description = "Operations related to members")
public class MemberController {
    private final MemberServiceImpl memberServiceImpl;

    public MemberController(MemberServiceImpl memberServiceImpl) {
        this.memberServiceImpl = memberServiceImpl;
    }

    @Operation(summary = "Create a new member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member created",
                    content = @Content(schema = @Schema(implementation = Member.class)))
    })
    @PostMapping
    public ResponseEntity<Member> create(@Valid @RequestBody Member m) {
        return ResponseEntity.ok(memberServiceImpl.create(m));
    }

    @Operation(summary = "List members with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of members",
                    content = @Content(schema = @Schema(implementation = Member.class)))
    })
    @GetMapping
    public ResponseEntity<Page<Member>> list(
            @Parameter(description = "Page index (0-based)") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(memberServiceImpl.list(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get a member by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Member> get(@Parameter(description = "Member id") @PathVariable("id") Long id) {
        return ResponseEntity.ok(memberServiceImpl.get(id));
    }

    @Operation(summary = "Update an existing member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member updated",
                    content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@Parameter(description = "Member id") @PathVariable("id") Long id, @Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberServiceImpl.update(id, member));
    }
}