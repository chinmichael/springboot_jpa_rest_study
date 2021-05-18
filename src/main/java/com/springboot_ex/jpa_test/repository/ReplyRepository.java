package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long>{

    // 게시물 삭제시 댓글들 삭제
    @Modifying // update, delete의 레코드 변경 실행을 위해 필요한 어노테이션
    @Query("delete from Reply r where r.board.bno = :bno") // Board 객체 참조 변수가 board였다
    void deleteByBno(Long bno);

    // 게시물 번호로 댓글 목록 가져오기 >> 쿼리메서드 이용함 (복수형이므로 Reply 객체의 List로 / Board를 기준으로 / Rno 순번으로)
    List<Reply> getRepliesByBoardOrderByRno(Board board);
    // 그 밖에 추가 / 수정 / 삭제는 bno가 아닌 rno를 기준으로 정렬 순서등도 없이 이뤄지므로 굳이 새로 추가하지 않고 상속받은 기본 JPA CRUD 메서드로
}
