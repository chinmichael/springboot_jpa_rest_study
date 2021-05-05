package com.springboot_ex.jpa_test.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.springboot_ex.jpa_test.entity.Guestbook;
import com.springboot_ex.jpa_test.entity.QGuestbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTest {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies() {

        IntStream.rangeClosed(1, 300).forEach(i -> {

            Guestbook guestbook = Guestbook.builder()
                    .title("Title...." + i)
                    .content("Content...." + i)
                    .writer("user" + (i % 10))
                    .build();

            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest() {

        Optional<Guestbook> result = guestbookRepository.findById(300L);

        if(result.isPresent()) {
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title....");
            guestbook.changeContent("Changed Content....");

            guestbookRepository.save(guestbook); // moddate도 자동적으로 업데이트 됨을 볼 수 있음
        }
    }

    //Querydsl 연습작성 테스트 (단일항목 검색)
    @Test
    public void testQuery1() { // title에 '1'이라는 글자가 있는 엔티티 검색

        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;
        //Q도메인 클래스를 가져와 기반이 된 엔티티클래스의 필드를 변수로 사용할 수 있게 처리
        //Q도메인 클래스를 보면 알 수 있지만 static final로 선언된 변수에 객체가 생성되어 있음

        String keyword = "1"; // 말그대로 검색 키워드

        BooleanBuilder builder = new BooleanBuilder();
        // where문에 들어가는 조건을 담는 컨테이너 정도로 생각 (뭐 찾아봐도 where조건 생성해주는 것 정도로 설명해주는 곳 많음)

        BooleanExpression expression = qGuestbook.title.contains(keyword);
        // 필드값과 결합하여 조건생성 위처럼 생성하는 것이 querydsl의 predicate타입 >> predicate타입만 BooleanBuilder의 파라미터로 들어갈 수 있음

        builder.and(expression); // and나 or 같은 조건 키워드와 결합하여 BooleanBuilder에 작성된 predicate를 추가함

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
        // GuestbookRepository I/F에 QuerydslPredicateExcutor가 추가 상속되었으므로 findAll()을 사용해 BooleanBuilder 실행 가능
        
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    //Querydsl 다중항목검색 테스트
    @Test
    public void testQuery2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String key = "1";

        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression exTitle = qGuestbook.title.contains(key); // like가 아니라 =이면 eq()
        BooleanExpression exContent = qGuestbook.content.contains(key);
        BooleanExpression exAll = exTitle.or(exContent); // 제목 '혹은' 내용에 키워드를 넣는 where
        // 쿼리문을 싹다 새로 쓰는게 아니라 경우에따라 조건추가가 필요하면 이렇게 추가시키면 된다.

        builder.and(exAll); // 위의 조건을 BooleanBuilder에 추가
        builder.and(qGuestbook.gno.gt(0L));
        // qGuestbook가 가져온 필드 gno에서 0보다 큰 조건gt(0L) 추가
        // 설마 얘들 jQuery처럼 eq(equal) / ne(not equal) / lt(little) / le(little or equal) / gt(greater) / ge 표현식 쓰남

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
           System.out.println(guestbook);
        });
    }

}
