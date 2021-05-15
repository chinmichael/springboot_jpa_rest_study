package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long>{

    @Modifying // update, delete의 레코드 변경 실행을 위해 필요한 어노테이션
    @Query("delete from Reply r where r.board.bno = :bno") // Board 객체 참조 변수가 board였다
    void deleteByBno(Long bno);

}
