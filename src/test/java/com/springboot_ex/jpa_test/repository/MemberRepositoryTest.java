package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertMember() { // 참조만 되는 PK가진 객체들 먼저 생성해준다
        IntStream.rangeClosed(1, 100).forEach(i -> {

            Member member = Member.builder()
                    .email("user" + i + "@aaa.com")
                    .password("111111")
                    .name("User"+i)
                    .build();

            memberRepository.save(member);
        });
    }
}
