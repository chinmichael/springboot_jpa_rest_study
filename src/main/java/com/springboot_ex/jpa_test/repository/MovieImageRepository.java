package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {
}
