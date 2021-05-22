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



     */


}
