package org.example.springbooks.service;

import org.example.springbooks.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    /**
     * Create a new member record.
     */
    Member create(Member m);

    /**
     * List all members with pagination support.
     */
    Page<Member> list(Pageable pageable);

    /**
     * Retrieve details of a specific member by ID.
     */
    Member get(Long id);

    /**
     * Update an existing member’s information.
     */
    Member update(Long id, Member updated);
}
