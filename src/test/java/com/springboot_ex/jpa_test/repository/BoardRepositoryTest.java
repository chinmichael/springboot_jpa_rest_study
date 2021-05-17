package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    @Test
    @Transactional
    public void testRead1() {
        Optional<Board> result = boardRepository.findById(100L);

        Board board = result.get();

        System.out.println(board);
        System.out.println(board.getWriter());
    }

    @Test
    public void testReadWithWriter() {
        Object result = boardRepository.getBoardWithWriter(100L);
        Object[] arr = (Object[]) result;
        System.out.println("=======================================");
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testGetBoardWithReply() {
        List<Object[]> result = boardRepository.getBoardWithReply(100L);
        for(Object[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }

    @Test
    public void testWithReplyCount() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Object[]> result = boardRepository.getBoardWithReplyCount(pageable);

        result.get().forEach(row -> {
            Object[] arr = (Object[])row;
            System.out.println(Arrays.toString(arr));
        });
    }

    @Test
    public void testRead3() {
        Object result = boardRepository.getBoardByBno((100L));
        Object[] arr = (Object[]) result;
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testSearch1() {
        boardRepository.search1();
    }

    @Test
    public void testSearch2() {
        boardRepository.search2();
    }

    @Test
    public void testSearchPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending().and(Sort.by("title").ascending()));

        Page<Object[]> result = boardRepository.searchPage("t", "1", pageable);
    }
}

/*  5/14 : Eager loading / Lazy loading

    @ManyToOne 처리를 한 경우 FK쪽 엔티티를 가져올 때 PK쪽 엔티티도 같이 가져옴(JOIN)

    Eager loading
    기본적으로 @ManyToOne
    JPA에서 연관관계를 어떻게 가져올 것인가(fetch)의 디폴트 모드는 Eager loading(즉시 로딩)

    >> 연관관계가 있는 모든 엔티티를 left outer join으로 데려온다
    >> 연관관계가 많거나 복잡할수록 조인에 의한 성능저하를 가져옴

    ====================================================================================================

    Lazy loading(지연로딩) (@ManyToOne(fetch=FetchType.LAZY) 로 속성을 명시적으로 지정해줘야함
    기존 Eager loading 방식의 testRead1을
    Lazy loading으로 처리하면, board테이블을 가져온 뒤 DB접속종료가 발생하기에 member테이블을 가져오는데 문제가 발생
    >> @Transactional를 붙여 필요할 때 다시 DB연결을 처리하도록 해야한다.

    >> 쿼리문을 확인하면 두 테이블을 조인하는 것이 아닌 board테이블만 로딩후
       board.getWriter()를 처리하기 위해 member테이블을 다시 연결함

    >> 기본 로딩은 하나의 테이블만 하고 필요할 때마다 해당 테이블을 로딩하기에 속도는 빠르지만
       연관관계가 많아지면 이쪽도 여러번 쿼리문을 실행해야하는 문제가 발생한다.

       >> 따라서 지연 로딩을 기본으로 세팅한 뒤 필요에 따라 JPQL로 join을 처리하는 등의 적절한 방법을 취한다
 */