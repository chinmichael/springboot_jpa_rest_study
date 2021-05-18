package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Reply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
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

    @Test
    public void readReply1() {
        Optional<Reply> result = replyRepository.findById(1L); // 쿼리문에서 연관된 모든 테이블(Member, Board)까지 조인됨

        Reply reply = result.get();

        System.out.println(reply); // 참조한 부분 제외해서 보여줌
        System.out.println(reply.getBoard()); // Board객체에 해당하는 부분은 이쪽으로 따로 가져와야한다.
    }

    @Test
    public void testListByBoard() {
        List<Reply> replyList = replyRepository.getRepliesByBoardOrderByRno(Board.builder().bno(97L).build());

        replyList.forEach(reply -> System.out.println(reply));
    }
}