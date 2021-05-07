package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Reply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class ReplyRepositoryTest {

    @Autowired
    ReplyRepository replyRepository;

    @Test
    public void insertReply() {

        IntStream.range(1, 300).forEach(i -> {

            long bno = (long)(Math.random() * 100) + 1; // 게시물들을 랜덤으로 지정해 댓글을 닮

            Board board = Board.builder().bno(bno).build();

            Reply reply = Reply.builder()
                    .text("Reply..." + i)
                    .board(board)
                    .replyer("guest")
                    .build();

            replyRepository.save(reply);
        });
    }
}
