package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieImage;
import com.springboot_ex.jpa_test.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {

    List<MovieImage> findByMovie(Movie movie);
}
