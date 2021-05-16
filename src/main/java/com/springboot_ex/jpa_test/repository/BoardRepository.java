package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {

    @Query("select b, w from Board b left join b.writer w where b.bno = :bno")
    Object getBoardWithWriter(@Param("bno") Long bno);

    //SQL : select board.bno, board.title, ~ , rno, text from board left outer join reply on reply.board_bno = board.bno where board.bno = 100L
    @Query("select b, r from Board b left join Reply r on r.board = b where b.bno = :bno")
    List<Object[]> getBoardWithReply(@Param("bno") Long bno);

    /*
    목록 만들기 예시
    >> Board(게시물 번호 / 제목 / 작성시간) / Member(회원 이름 / 이메일) / Reply(해당 게시물 댓글 수)
    >> 세 엔티티 중 'Board의 Data가 가장 많으므로' Board를 중심으로 조인 작성
    >> 게시물 당 댓글 수 이므로 Board의 번호를 기준으로 GROUP BY 처리

    group by 다시 체크
    조회된 데이터 기준에 따라 묶어준다 >> 일반적으로 집계함수와 같이 쓴다. (이 경우 댓글들을 게시글을 기준으로 묶어 세어야 하므로)
    group by를 통해 레코드를 묶어 집계함수를 썼으므로 여기서 필요하면 where이 아닌 having을 사용
     */
    @Query(value = "select b, w, count(r) from Board b left join b.writer w left join Reply r on r.board = b group by b",
            countQuery="select count(b) from Board b")
    Page<Object[]> getBoardWithReplyCount(Pageable pageable);

    /*
    게시물 조회 쿼리
    >> 보통 댓글은 개수만 알려주고 필요하면 ajax로 요청해 동적으로 가져온다
    >> 집계함수가 있는데 왠 where? 이 아니라 이 경우는 그룹으로 묶은 집계함수가 아니라 조회된 board에서 해당 reply를 걍 죄다 묶었으니 가능
     */
    @Query("select b, w, count(r) from Board b left join b.writer w left join Reply r on r.board = b where b.bno = :bno")
    Object getBoardByBno(@Param("bno") Long bno);
}

/*  5/14 : JPQL로 JOIN 처리
    >> Lazy loading 처리 후 조인이 필요할 때 가장 보편적으로 처리하는 방법

    (참고로 스프링부트 2부턴 연관관계가 없어도 JPA에서 조인처리가 가능하다)

    >> Board에서 Member는 / Board측이 FK로 참조를 하기에 / Board입장에서 연관관계 존재 : left join으로 처리
    >> 조인 조건 지정하는 on이 없어도 알아서 JPA가 처리함 (b.writer w를 그냥 알아서 pk, fk기준으로 join해버림)

    >> Board에서 Reply는 / Reply에서 FK로 참조하는 것이기에 / Board입장에서만 볼때는 Reply를 참조하지 않음 : left join on 으로 처리
    >> 순수 SQL처럼 'on'을 넣어 조인조건을 직접적으로 지정해줘야 한다(Reply에서 뭐로 Board 참조하는지 알려줘야함) (r.board = b / reply.board_bno = board.bno)


    조인 개념 다시 메모
    JOIN : 두 개 이상 테이블을 묶어 새 집합을 만들어내는 것

    (INNER) JOIN : 일반적으로 지칭하는 조인
                 : SELECT ~ FROM 테이블 (INNER) JOIN 엮을 테이블 ON 조인 조건 WHERE ~ (검색항목이 다른 테이블에도 존재할 경우 테이블명(별칭).칼럼명 으로 명시)
                 : 조건(on)이 부합하는 행에 대해서만 조인이 발생

    OUTER JOIN  : INNER JOIN과 달리 조건에 부합하지 않은 행을 포함해 결합
                : 부합하지 않아도 되는 테이블의 위치에 따라 LEFT / RIGHT / FULL(양쪽>>모든 행이 조건없이 결합) (OUTER) JOIN이 있음
 */