package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieImageRepository movieImageRepository;

    @Commit
    @Transactional
    //Movie와 MovieImage들이 같은 시점에 insert되야하므로 (근데 우선 Movie 레코드가 생성되고 PK가 생성되야함)
    @Test
    public void insertMovies() {

        IntStream.rangeClosed(1, 100).forEach(i-> { // 100편의 영화샘플로 연관이미지 등록

            Movie movie = Movie.builder().title("Movie..." + i).build();

            System.out.println("=====================================");

            movieRepository.save(movie);

            int count = (int)(Math.random() * 5) + 1; // 영화 이미지 개수는 영화마다 다를 수 있으므로

            for(int j = 0; j < count; j++) {

                MovieImage movieImage = MovieImage.builder()
                        .uuid(UUID.randomUUID().toString())
                        .movie(movie)
                        .imgName("test"+j+".jpg").build();
                
                movieImageRepository.save(movieImage);

                System.out.println("=====================================");
            }

        });

    }
}
