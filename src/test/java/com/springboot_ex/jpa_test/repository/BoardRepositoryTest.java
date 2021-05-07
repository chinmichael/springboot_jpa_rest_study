package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Test
    public void insertBoard() {

        IntStream.rangeClosed(1, 100).forEach(i -> {

            Member member = Member.builder().email("user"+i+"@aaa.com").build();

            Board board = Board.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer(member) // 필드가 아닌 객체를 참조했음
                    .build();

            boardRepository.save(board); // select문으로 참조하는 해당 member레코드를 검색한 뒤에 insert가 이뤄짐
        });
    }
}
