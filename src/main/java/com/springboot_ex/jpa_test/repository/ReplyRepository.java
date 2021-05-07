package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>{
}
