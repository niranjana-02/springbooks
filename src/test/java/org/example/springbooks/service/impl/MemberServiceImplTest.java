package org.example.springbooks.service.impl;

import org.example.springbooks.exception.ResourceNotFoundException;
import org.example.springbooks.model.Member;
import org.example.springbooks.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberServiceImpl memberServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberServiceImpl = new MemberServiceImpl(memberRepository);
    }

    @Test
    void create_shouldSaveAndReturnMember() {
        Member input = Member.builder().name("Niranjana").email("niranjana@example.com").build();
        Member saved = Member.builder().id(1L).name("Niranjana").email("niranjana@example.com").build();

        when(memberRepository.save(input)).thenReturn(saved);

        Member result = memberServiceImpl.create(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Niranjana", result.getName());
        verify(memberRepository, times(1)).save(input);
    }

    @Test
    void list_shouldReturnPagedMembers() {
        Member m1 = Member.builder().id(1L).name("Alice").email("alice@example.com").build();
        Member m2 = Member.builder().id(2L).name("Bob").email("bob@example.com").build();
        Page<Member> page = new PageImpl<>(List.of(m1, m2));

        when(memberRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<Member> result = memberServiceImpl.list(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getName());
        verify(memberRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void get_existingMember_shouldReturnMember() {
        Member m = Member.builder().id(1L).name("John").email("john@example.com").build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(m));

        Member result = memberServiceImpl.get(1L);

        assertEquals("John", result.getName());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void get_nonexistentMember_shouldThrow() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberServiceImpl.get(99L));
        verify(memberRepository, times(1)).findById(99L);
    }

    @Test
    void update_existingMember_shouldSaveUpdatedData() {
        Member existing = Member.builder().id(1L).name("Old Name").email("old@example.com").build();
        Member updated = Member.builder().name("New Name").email("new@example.com").build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepository.save(existing)).thenReturn(existing);

        Member result = memberServiceImpl.update(1L, updated);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(memberRepository, times(1)).save(existing);
    }

    @Test
    void update_nonexistentMember_shouldThrow() {
        Member updated = Member.builder().name("New Name").email("new@example.com").build();

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberServiceImpl.update(1L, updated));
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void list_emptyPage_shouldReturnEmptyResult() {
        Page<Member> emptyPage = new PageImpl<>(Collections.emptyList());

        when(memberRepository.findAll(PageRequest.of(0, 5))).thenReturn(emptyPage);

        Page<Member> result = memberServiceImpl.list(PageRequest.of(0, 5));

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(PageRequest.of(0, 5));
    }
}
