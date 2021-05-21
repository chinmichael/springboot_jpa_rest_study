package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
//@Table(name="m_member") // 만약 다른 프로젝트에 Member로 엔티티를 만들었었다면 DB상 기존 게시판 Member와 충돌했었기에 이름 바꿈
public class MovieMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;

    private String email;

    private String pw;

    private String nickname;
}
