package com.springboot_ex.jpa_test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReplyDTO {

    @ApiModelProperty(value="댓글번호")
    private Long rno;

    @ApiModelProperty(value="댓글내용")
    private String text;

    @ApiModelProperty(value="댓글작성자")
    private String replyer;

    @ApiModelProperty(value="게시물번호")
    private Long bno; // Entity 클래스처럼 Board 객체가 아닌 실제로 운반할 게시물 번호

    private LocalDateTime regDate, modDate;

}
