package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "movie")
public class MovieImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum;

    private String uuid; // 이미지 고유번호 아래 경로에 최종 식별

    private String imgName;

    private String path; // 년/월/일 폴더 구조

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}
