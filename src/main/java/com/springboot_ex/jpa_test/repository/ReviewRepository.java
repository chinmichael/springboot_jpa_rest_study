package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
