package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
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

    @Test
    public void testListPage() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "mno"));

        Page<Object[]> result = movieRepository.getListPage(pageRequest);

        for(Object[] objs : result.getContent()) {
            System.out.println(Arrays.toString(objs));
        }
    }

    @Test
    public void testGetMovieWithAll() {
        List<Object[]> result = movieRepository.getMovieWithAll(92L);

        System.out.println(result);

        for(Object[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }

    @Test
    @Transactional
    public void testGetMovieWithAll2() {
        List<Object[]> result1 = movieRepository.getMovieWithReview(92L);

        Movie movie = Movie.builder().mno(92L).build();
        List<MovieImage> result2 = movieImageRepository.findByMovie(movie);

        for(Object[] arr : result1) {
            System.out.println(Arrays.toString(arr));
        }
        for (MovieImage mi : result2) {
            System.out.println(mi);
        }
    }
}
