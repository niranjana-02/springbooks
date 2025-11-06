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

    public Member create(Member m) {
        return memberRepository.save(m);
    }

    public Page<Member> list(Pageable p) {
        return memberRepository.findAll(p);
    }

    public Member get(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    public Member update(Long id, Member updated) {
        Member m = get(id);
        m.setName(updated.getName());
        m.setEmail(updated.getEmail());
        return memberRepository.save(m);
    }
}
