package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository; // 테스트 대상 의존성 주입

    @Test
    public void testClass() {
        System.out.println(memoRepository.getClass().getName()); // AOP에 따라(그래서 Proxy~달림) 자동으로 스프링 내부에 생성되는 클래스명 출력 확인
    }

    /*@Test
    public void testInsertDummies() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
           Memo memo = Memo.builder().memoText("Sample..."+i).build();
           //Builder 패턴 다시 정리 MemoBuilder클래스에서 memoText를 넘겨받은 (mno는 생성전략으로 알아서처리) 뒤 build()로 Memo객체 생성 후 넘김
           memoRepository.save(memo);
        });
    }*/

    /*@Test
    public void testSelect() {
        Long mno = 100L;

        System.out.println("findById()방식");
        Optional<Memo> result = memoRepository.findById(mno);
        // java.util의 Optional 타입으로 반환되어 저장
        // 로그상 SQL처리는 findById시에 바로 처리됨

        System.out.println("========================================");

        if(result.isPresent()) { // 반환값이 있는지 확인 가능
            Memo memo = result.get();
            System.out.println(memo);
        }
        System.out.println();
    }*/

    /*@Transactional //getOne은 트랜잭션 처리를 위한 어노테이션 필요
    @Test
    public void testSelect2() {
        Long mno = 100L;

        Memo memo = memoRepository.getOne(mno);
        System.out.println("========================================");
        System.out.println(memo);

        //로그 상 ==== 아래에서 SQL처리와 함께 출력값이 나옴
        //이는 실제 객체가 필요한 순간이 되어야 SQL처리를 함을 알 수 있음
    }*/

    /*@Test
    public void testUpdate() { // save()는 해당 레코드 확인후 이미 있음 update 없음 insert (SQL문을 보면 select가 함께 처리되어 있음)
        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();
        System.out.println(memoRepository.save(memo));
    }*/

    /*@Test
    public void testDelete() {
        Long mno = 100L;

        memoRepository.deleteById(mno);

        //만약 해당 데이터가 존재하지 않으면(select가 먼저 실행되어 데이터를 확인함) 예외발생되게 처리되어 있음(EmptyResultDataAccessException)
    }*/

    /*@Test
    public void testPageDefault() {
        Pageable pageable = PageRequest.of(0, 10); // 1페이지의 데이터 (페이지 크기는 10목록)
        // Pageable 인터페이스를 PageRequest 클래스의 인스턴스로 구현, 인스턴스 생성은 of() static method로

        Page<Memo> result = memoRepository.findAll(pageable);
        System.out.println(result);
        //실제 처리 SQL을 보면 목록을 가져오는 쿼리 뿐 아니라 전체 데이터 개수를 가져오는 쿼리를 함께 처리함 볼 수 있음
        //기존에 공부했던 쿼리에서도 pagingList쿼리와 함께 pagingListCnt쿼리를 함께 세트로 한 것을 생각해보면 됨
        System.out.println();
        System.out.println("=========================================");

        System.out.println("Total Page : " + result.getTotalPages());
        System.out.println("Total Count : " + result.getTotalElements());
        System.out.println("Page Number : " + result.getNumber());
        System.out.println("Page Size : " + result.getSize()); //페이지 당 데이터 개수
        System.out.println("Has Next Page ? : " + result.hasNext()); // 다음 페이지 존재여부
        System.out.println("First Page? : " + result.isFirst());

        System.out.println();
        System.out.println("=========================================");
        for(Memo memo : result.getContent()) { // .getContent() : List<Entity>로 리스트 가져옴
            System.out.println(memo);
        }
    }*/

    /*@Test
    public void testSort() {

        Sort sort1 = Sort.by("mno").descending(); //desc로 정렬
        Pageable pageable = PageRequest.of(0, 10, sort1);
        Page<Memo> result = memoRepository.findAll(pageable); //SQL에 order by 절이 추가됨을 알 수 있음

        result.get().forEach(memo -> { //생각해보니 여기도 arrow함수가 있네 (익명함수)
            System.out.println(memo);
        });

        System.out.println("============================================");

        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // 여러 정렬조건을 정한뒤 and()메서드로 연결해서 한번에 박을 수 있다 (우선순위 순으로)

        Pageable pageable2 = PageRequest.of(0, 10, sortAll);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }*/

    /*@Test
    public void testQueryMethods() {

        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);

        for(Memo memo : list) {
            System.out.println(memo);
        }
    }*/

    /*@Test
    public void testQueryMethodsWithPageable() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());

        Page<Memo> result = memoRepository.findByMnoBetween(10L, 50L, pageable);

        result.get().forEach(memo -> System.out.println(memo));
        //페이지 사이즈가 1페이지당 10개씩 보여주므로 50~41
    }*/

    /*@Commit
    @Transactional
    @Test
    public void testDeleteQueryMethod() {
        memoRepository.deleteMemoByMnoLessThan(10L);
    }*/

}

/*
JpaRepository Crud메서드
insert, update : save(Entity entity) >> JPA구현체가 메모리상 객체를 비교해 없으면 insert 있음 update
select : findById(Key타입 key), getOne(Key타입 key)
deledt : deleteById(Key타입 key), delete(Entity entity)
 */

/*
DBMS 페이징 처리 : Oracle>inline View MySQL>limit

JPA 페이징 처리 : 내부적으로 Dialect를 이용해처리(DB에 맞게) >> SQL이 아닌 API의 객체와 메서드로 페이징처리 가능
페이징 및 정렬관련 메서드는 Crud하위 Jpa상위인 PagingAndSortRepository의 메서드를 이용
findAll() 메서드를 활용한다

findAll(Pageable pageable) > 반환타입 Page<T>
findAll(Sort sort) > 반환타입 Iterable<T>    >> 파라미터 타입에 따라 반환값이 달라지니 주의

Pageable 인터페이스 = JPA 페이지처리를 위한 가장 중요한 존재 >> 페이지처리에 필요한 정보를 전달
Pageable의 구현체는 PageRequest 클래스 >> 생성자의 접근제어자가 protected >> new로 생성 못함
PageRequest는 page(지정 페이지 번호, 0을 시작점으로 함), size(페이지당 리스트 개수), sort 정보를 통해 static 메서드인 of()통해 생성

of(int page, int size)
of(int page, int size, Sotrt.Direction direction, String props1, ... , String propsN) 정렬방향 + 정렬기준 필드
of(int page, int size, Sort sort)
 */