package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
