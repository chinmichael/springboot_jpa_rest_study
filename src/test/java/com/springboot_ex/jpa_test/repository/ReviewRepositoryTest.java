package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieMember;
import com.springboot_ex.jpa_test.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

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
}
