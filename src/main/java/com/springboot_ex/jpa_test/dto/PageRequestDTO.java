package com.springboot_ex.jpa_test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Getter // @Setter는 page에 음수값 등 오류를 피하기 위해 뺌
public class PageRequestDTO {

    private int page; // 음수값 등이 들어올 수 없게 추가 설계 필요
    private int size;

    private String type;
    private String keyword;

    public PageRequestDTO() { // 멤버변수 null 등의 문제 막기 위해 @NoArgsConstructor안씀
        this.page = 1;
        this.size = 10;
        // 기본값을 가지는 것이 좋으므로 @NoArgsConstructor 대신 멤버변수에 값을 넣어주는 기본생성자 세팅
    }

    public Pageable getPageable(Sort sort) {
        // page와 sort는 builder로 가져오고 sort만 지정
        // PageRequstDTO의 주요 목적 : JPA에서 사용하기 위한 Pageable을 생성
        
        return PageRequest.of(page - 1, size, sort);
        // 실제 JPA에서 페이지번호가 0부터 시작하므로
    }
}

/*  JPA DTO 페이징처리 순서
    1 화면에 필요한 List Data에 대한 DTO 생성
    2 DTO를 Pageable타입을 전환
    3 Page<Entity>를 DTO List로 전환
    4 페이지번호처리

    페이징처리는 게시판의 오메가이므로 DTO는 당연 재사용가능하게 설정
    개인적으로 예전에는 추상클래스 만들어서 상속 시켰지만 아예 지네릭스를 사용하면 더 꿀(?)이 된다
 */

/*  PageRequestDTO : 현재 페이지 / 페이지 사이즈 / 정렬 + 그 밖에 정렬 조건 등
                     페이징 요청을 위한 공통적인 데이터 파라미터 가짐
                     
                     >> 이를 가지고 JPA에서 사용하기 위한 Pageable 객체 생성

 */