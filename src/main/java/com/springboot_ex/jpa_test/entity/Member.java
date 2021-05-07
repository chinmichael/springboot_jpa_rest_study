package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor  // 이메일을 PK로 하는 입장에서 두 생성자@는 경우에 따라 골치 아플 수 있지만 우선 DTO로 필터링을 할 예저이므로
@Getter
@ToString
public class Member extends BaseEntity {

    @Id
    private String email;

    private String password;

    private String name;
}
