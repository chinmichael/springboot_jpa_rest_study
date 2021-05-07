package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
