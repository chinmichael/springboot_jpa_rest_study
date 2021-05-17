package com.springboot_ex.jpa_test.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.QBoard;
import com.springboot_ex.jpa_test.entity.QMember;
import com.springboot_ex.jpa_test.entity.QReply;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

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

            // order by 처리
            // tuple.orderBy(board.bno.desc());
            // 위와 같이 처리하면 매우 간단하지만 동적 쿼리 구성을 위해 Querydsl까지 동원하는데 있어 장점이 퇴색하기에 복잡해도 아래처럼 한다

            Sort sort = pageable.getSort();
            // Pageable에서 가져온 Sort 객체는 JPQL에서 지원하지 않음 >> 지원하는 OrderSpecifier<T extends Comparable>로 변환해야함
            // OrderSpecifier()의 파라미터로는 Sort()객체에 넣은 (정렬 방향, 기준 컬럼)을 넣는다

            sort.stream().forEach(order -> {
                // Sort객체는 내부적으로 여러 Sort객체를 연결(담는)하는게 가능
                // (쉽게말해 getSort()로 얻어온 객체 하나에 order by에 넣을 여러 정렬 쿼리가 다 있고 분리 가능)
                // >> Sort.by("bno").descending().and(Sort.by("title").ascending() 이렇게 담긴 Sort를 분리해 읽어내야 한다는 것

                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                // 딱봐도 뽑은 Sort객체의 방향을 체크

                String prop = order.getProperty();
                // .by()에 담은 정렬 기준 컬럼 추출

                PathBuilder orderByExpression = new PathBuilder(Board.class, "board"); // 이때 변수명은 JPQLQuery 생성시 사용한 변수명과 동일해야 한다
                // 정렬 기준이 되는 컬럼을 구성하기 위해 PathBuilder로 해당 객체 클래스 정보를 가져와 get()메서드로 위에서 가져온 컬럼을 세팅한다

                tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
            });

            tuple.groupBy(board);

            // 페이징처리 >> Pageable로 파라미터를 전달받았기에 위 Sort객체 처리 뿐 아니라 offset과 pagesize를 JPQLQuery의 각 메서드에 대입 가능
            tuple.offset(pageable.getOffset()); // offset은 현재페이지 * 사이즈 이다
            tuple.limit(pageable.getPageSize());
            

            List<Tuple> result = tuple.fetch();

            log.info(result);
            
            long count = tuple.fetchCount(); // countQuery를 처리해준다
            
            log.info("Count : " +count);
            
            return new PageImpl<Object[]>(
                    result.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, count
            );
            
            // 리턴타입이 Page<Object[]>이므로 Page 타입 객체 생성
            // Page는 I/F이므로 구현 클래스인 PageImpl타입의 객체를 생성해야함
            // PageImpl의 생성자는 PageImpl(List<T> content)와 PageImpl(List<T> content, Pageable pageable, long total)임
            // List전환부는 개인 자바 공부에서 컬렉션 프레임워크 담에 다시 stream 진행하면서 추가예정
            // total은 조회결과 레코드개수 즉 countQuery결과인 fetchCount()값
        }

        return null;
    }
}
