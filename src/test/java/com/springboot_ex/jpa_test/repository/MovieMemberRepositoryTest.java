package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.MovieMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class MovieMemberRepositoryTest {

    @Autowired
    private MovieMemberRepository movieMemberRepository;

    @Test
    public void insertMember() {

        IntStream.rangeClosed(1,100).forEach(i->{
            MovieMember movieMember = MovieMember.builder()
                    .email("review"+i+"@jpatest.com")
                    .pw("1111")
                    .nickname("reviewer" + i)
                    .build();

            movieMemberRepository.save(movieMember);
        });

    }
}
