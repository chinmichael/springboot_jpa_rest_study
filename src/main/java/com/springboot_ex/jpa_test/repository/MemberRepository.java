package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
