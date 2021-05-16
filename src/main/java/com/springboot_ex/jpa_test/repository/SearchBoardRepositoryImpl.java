package com.springboot_ex.jpa_test.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.QBoard;
import com.springboot_ex.jpa_test.entity.QMember;
import com.springboot_ex.jpa_test.entity.QReply;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {

    public SearchBoardRepositoryImpl() {
        super(Board.class);
    }
    /*
    1.클래스 구현시 중요한 점은 QuerydslRepositorySupport를 상속해야하는 것
    2.그리고 또한 QuerydslRepositorySupport의 not null인 도메인 클래스를 파라미터로 하는 생성자가 있어
    3.이를 super()로 호출해 해당 도메인 클래스의 클래스 객체를 지정한다.
      (클래스 객체를 얻는 방법 Class obj = new Object().getClass() or Object.class or Class.forName("Object")
      (위 super() 부분을 들어가보면 앎)
      (클래스 객체 : 클래스당 한개만 존재하는 클래스의 모든 정보를 담은 객체)

     */

    @Override
    public Board search1() {

        log.info("search1.......");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> jpqlQuery = from(board); // Querydsl라이브러리가 활용하는 JPQLQuery I/F
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));
        // 마찬가지 직관적인 join(), leftJoin(), rightJoin() 함수
        // 필요시 조인조건을 on()으로 처리해주면 됨
        
        jpqlQuery.select(board).where(board.bno.eq(1L)); // 메서드가 매우 직관적이라 어떤 JPQL이 짜지는지 알 수 있다

        log.info("-------------------------------");
        log.info(jpqlQuery); // 동작해야하는 JPQL의 문자열 확인 가능 select board from board where board.bno=1 등
        log.info("-------------------------------");

        List<Board> result = jpqlQuery.fetch();

        return null;
    }

    @Override
    public Board search2() {

        log.info("search2........");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board); // 일단 기준 from 테이블에 맞춘 지네릭으로 JPQLQuery<>을 선언처리후에 밑에 Tuple로 가는거
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board)); // JPQL에서는 조인조건이 PK, FK필드가 아닌 객체로 움직이는거 항상 명심

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member.email, reply.count());
        // select시 board와 같은 객체 단위가 아닌 저런 세세한 필드 데이터를 추출할 경우 Querydsl 라이브러리의 Tuple객체를 이용
        tuple.groupBy(board);
        // tuple을 적용한 뒤의 groupBy()로 묶는다
        // 만약 객체 단위로 움직였다면 기존 jpqlQuery.select(board).groupBy(board) 셀렉트 뒤에 붙여 수행

        log.info("---------------------------------------------------");
        log.info(tuple);
        log.info("---------------------------------------------------");

        List<Tuple> result = tuple.fetch();

        log.info(result); // Board객체 / 이메일 / 댓글 수가 출력

        return null;
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {

        // Repository 영역에서는 가능한 DTO를 다루지 않는다 (DTO는 Service와 Controller에서 데이터 전송 담고(Repo>Service) (클라이언트>Controller)을 위해)

        log.info("searchPage...................................");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member, reply.count());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = board.bno.gt(0L); // greater

        booleanBuilder.and(expression);

        if(type != null) {
            String[] typeArr = type.split("");

            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for(String t : typeArr) {
                switch(t) {
                    case "t":
                        conditionBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        conditionBuilder.or(member.email.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(board.content.contains(keyword));
                        break;
                }
                booleanBuilder.and(conditionBuilder);
            }
            // Querydsl의 가장 큰 장점 : 동적으로 검색조건을 추가할 수 있음
            // 위 코드에서는 String t를 각 문자로 split()하여 t(타이틀)w(작성자)c(내용)이 어떻게 구성되어 있는지 확인해
            // 각 BooleanBuilder를 or로 연결시킨 후 마지막에 where()에 삽입한다.

            tuple.where(booleanBuilder);

            // JPQL의 orderBy()애서 JPQL은 Pageable의 Sort객체를 지원하지 않기 때문에 OrderSpecifier<T extends Comparable>을 파라미터로 해야함

            tuple.groupBy(board);

            List<Tuple> result = tuple.fetch();

            log.info(result);

            return null;
        }

        return null;
    }
}
