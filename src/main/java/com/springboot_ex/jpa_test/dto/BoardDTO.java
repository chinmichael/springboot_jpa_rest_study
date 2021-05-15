package com.springboot_ex.jpa_test.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long bno;
    private String title;
    private String content;
    private String writerEmail;
    private String writerName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int replyCount;

    // DTO는 컨테이너의 입장 >> 엔티티와 달리 화면으로 보내거나 가져오는 데이터를 기준으로 구성한다.
}
