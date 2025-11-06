package org.example.springbooks.service.impl;

import org.example.springbooks.model.Member;
import org.example.springbooks.repository.MemberRepository;
import org.example.springbooks.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MemberServiceImpl {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member create(Member member) {
        return memberRepository.save(member);
    }

    public Page<Member> list(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Member get(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    public Member update(Long id, Member updated) {
        Member member = get(id);
        member.setName(updated.getName());
        member.setEmail(updated.getEmail());
        return memberRepository.save(member);
    }
}
