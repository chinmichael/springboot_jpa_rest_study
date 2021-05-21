package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.MovieMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieMemberRepository extends JpaRepository<MovieMember, Long> {
}
