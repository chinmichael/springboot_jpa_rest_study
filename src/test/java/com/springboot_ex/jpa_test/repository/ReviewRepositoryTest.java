package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieMember;
import com.springboot_ex.jpa_test.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieMemberRepository movieMemberRepository;

    @Test
    public void insertMovieReviews() {

        IntStream.rangeClosed(1,200).forEach(i -> {

            Long mno = (long)(Math.random()*100) + 1;
            Movie movie = Movie.builder().mno(mno).build();

            Long mid = (long)(Math.random()*100) + 1;
            MovieMember movieMember = MovieMember.builder().mid(mid).build();

            // 실제 기반 테이블의 레코드를 참조해야하기 때문에 Movie와 MovieMember 테스트더미를 만든 뒤 Review의 테스트 더미를 만든다

            int grade = (int)(Math.random()*5) + 1;

            Review movieReview = Review.builder()
                    .movieMember(movieMember)
                    .movie(movie)
                    .grade(grade)
                    .text("이 영화에 대한 느낌..." + i)
                    .build();

            reviewRepository.save(movieReview);
        });
    }

    @Test
    public void testGetMovieReviews() {
        Movie movie = Movie.builder().mno(92L).build();

        List<Review> result = reviewRepository.findByMovie(movie);

        result.forEach(movieReview -> {
            System.out.println(movieReview.getReviewnum());
            System.out.println("\t"+movieReview.getGrade());
            System.out.println("\t"+movieReview.getText());
            System.out.println("\t"+movieReview.getMovieMember().getEmail());
            System.out.println("===================================");
        });
    }

    @Test
    @Commit
    @Transactional
    public void testDeleteByMovieMember() {

        Long mid = 1L;

        MovieMember movieMember = MovieMember.builder().mid(mid).build();

        //순서 주의
        reviewRepository.deleteByMovieMember(movieMember);
        movieMemberRepository.deleteById(mid);
    }

    /*  연관관계가 있는 엔티티의 레코드 삭제시 주의할 점

        PK파트가 삭제되기 전에 FK로 참조하는 레코드가 먼저 지워져야 한다
        그리고 한 트랜잭션에 처리가 되도록 @Transaction을 선언부에 처리한다

        트랜잭션 성질ACID
        1.원자성(한 트랜잭션 실행 작업은 commit or rollback) 2.일관성 3.격리성 4.지속성

        @Transactional : 스프링에서 지원하는 선언적 트랜잭션 처리
        클래스, 메서드에 위 어노테이션 추가시, 트랜잭션 기능이 추가된 프록시 객체가 생성

        위 프록시 객체는 해당 메서드가 호출될 때 PlatformTransactionManager을 사용하여 트랜잭션을 시작하고 commit 혹은 rollback처리함

        프록시 객체가 나왔으니
        역시 Spring AOP를 기반으로 동작

        >> private 메서드는 사용불가 : 프록시 객체 생성을 위한 상속처리가 필요하기 때문에 private은 상속이 안되잖...
        >> 트랜잭션이 아닌 메서드가 호출될 때 해당 메서드에서 / 트랜잭션이 선언된 메서드를 내부호출할 경우 / 프록시객체가 아닌 대상객체Target Object의 메서드를 호출하기 때문에 적용되지 않음

        ================================================================================================================
        JPA의 객체 변경감지는 트랜잭션이 커밋될 때 작동한다
        (https://mommoo.tistory.com/92)에서
        Spring은 @Transactional 선언 메서드가 실행 전 transaction begin코드를 실행 후 transaction commit코드를 삽입해 변경감지를 유도한다

        참고로 스프링에서 AOP가 프록시패턴을 기반으로 구현될때
        I/F를 구현한 객체는 Dynamic Proxy를 아닌 경우는 CGLib방식으로 처리된다

        Proxy 패턴 : 대상 객체 기능을 대신 수행하는 대리객체를 사용하는 방식 (디자인패턴 공부하며 추가하자)
        https://velog.io/@pond1029/Transactional 의 그림참조
        사용자는 대상객체와 직접적으로 상호작용하는 것이 아닌 이를 감싼 대리객체인 프록시객체를 통해 이뤄짐
        내부에서 부가기능(핵심기능과 달리 '예외처리나' 입출력 로깅 'DB연결 등' 수행)을 거쳐 대상객체의 핵심로직을 수행후 Commit, Rollback처리

        Dynamic Proxy : Spring AOP 기본 전략 대상 객체 I/F를 구현한(리플렉션방식) 프록시 객체를 사용
        CGLib : 클래스의 바이트코드를 조작하여 프록시객체를 만들어주는 라이브러리 extends방식으로 프록시객체 구현 성능이 좋고 문제점이 개선되어 Spring Boot 기본전략
        ================================================================================================================

        다수 트랜잭션 경쟁시 발생문제 (한 트랜잭션 처리중 커밋or롤백 되기 전에 다른 트랜잭션이 해당 레코드에 접근한 경우)
        트랜잭션 a가 작업중 다른 트랜잭션 b의 접근을 허용하게 되버릴때 터지는 문제
        dirty read : 만약 a가 처리중이던 사항이 읽힌 후 롤백되어버리면 결과적으로 b가 잘못된 데이터를 읽어 데이터 불일치가 발생하는 것
        non-repeatable read : a가 한 트랜잭션의 한 작업에서 읽은 어떤 레코드가 다음 작업에서 읽기 전에 b가 수정시켜 커밋해버려 데이터 불일치가 발생
        phantom read : 위와 달리 일정 범위의 레코드를 읽는 사이 b가 그 범위의 값을 추가 시켰을때 다음 읽어들이는 결과에 불일치가 발생하는 것

        @Transactional 속성
        1. isolation (격리 수준이 올라갈 수록 성능저하가능)
        - DEFAULT : DB를 설정 Level을 따름
        - READ_UNCOMMITED : 커밋되지 않은 데이터 읽기 허용 dirty read 발생가능
        - READ_COMMITED : 커밋 확정된 데이터만을 읽음
        - REPEATABLE_READ : 트랜잭션 완료까지 해당 트랜잭션의 select문이 '읽은' 모든 데이터에 shared lock을 걸음 non-repeatable read 방지
        - SERIALIZABLE : 데이터 일관성 및 동시성을 위해 MVCC(Multi Version Concurrency Control) 사용하지 않음
                         해당 트랜잭션 select문이 읽어들일 영역에 shared lock을 걸음 phantom read 방지

        MVCC : 다중사용자 DB의 성능향상을 위해 데이터 조회시 Lock을 사용하지 않고 데이터 버전을 통한 동시성과 일관성을 높이는 기술

        2.propagation : 전파옵션 : 트랜잭션 도중 다른 트랜잭션을 호출하여 실행하는 상황에 대한 선택
        >> 호출된 트랜잭션이 호출한 트랜잭션을 그대로 사용하거나 새롭게 생성하는 등
        - REQUIRED : 디폴트 / 부모트랜잭션 내 실행 없으면 새로 생성
        - SUPPORTS : 이미 시작된 트랜잭션이 있으면 참여 아니면 트랜잭션 없이 진행
        - REQUIRES_NEW : 무조건 새로 트랜잭션 생성
        - MANDATORY : REQUIRED와 비슷하나 부모 트랜잭션이 없으면 예외 발생 (독립적으로 진행하면 안되는 경우)
        - NOT_SUPPORTED : 트랜잭션을 사용하지 않게 함 / 이미 진행중인 것이 있으면 보류시킴
        - NEVER : 위 레벨에서 진행중인 것이 있음 아예 예외를 발생시킴
        - NESTED : 이미 진행 중인 트랜잭션이 있으면 내부에 다시 트랜잭션을 생성
                 : 중첩 트랜잭션은 부모의 커밋 롤백에 영향을 받지만 역의 경우는 영향을 주지 않음  >> 로그 작업을 남기는 경우 유용 (로그가 롤백된다고 부모가 롤백되진 않고 부모가 롤백되면 알아서 롤백됨)

        3.readOnly = true or false
        - 성능 최적화 or 조회가 아닌 변경 작업을 의도적으로 방지하기 위해 (물론 트랜잭션매니저에 따라 무시할 수 있기에 주의)

        4.트랜잭션 롤백예외
        >> @Transactional은 런타임예외 발생시 롤백함 (체크 예외에서는 그냥 커밋 > 리턴값을 대신해 비즈니스적 의미를 담은 결과를 돌려주기 때문이라고 함)
        +> 스프링에서 데이터 액세스 예외는 런타임예외로 전환되기에 런타임예외에만 롤백처리를 한것

        rollbackFor로 예외 클래스(.class)를 넣거나 rollbackForClassName으로 예외 이름을 넣음 해당 Exception에 강제 롤백 가능
        <> noRollbackFor

        5.timeout = 초 (default는 -1 / no timeout) : 딱봐도 지정 시간 넘어가면 롤백처리
     */


}
