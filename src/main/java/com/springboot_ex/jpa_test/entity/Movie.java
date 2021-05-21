package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;

    private String title;
}

/*  5/21 : M:N(다대다) 관계 JPA 설계

    예를 들어
    한 편의 '영화'는 여러 회원에 의해 평가될 수 있음 | 한 명의 '회원'은 여러 영화를 평가할 수 있음

    >> 기존 1:M의 관계인 게시물:댓글의 관계의 경우 하나의 게시글에 댓글이 속하는 관계라면
       M:N의 영화:회원의 관계는 서로 독립적인 존재

    >> 이런 M:N의 관계는 상당히 많으나 (ex: 학생&수업 | 상품&상품카테고리 | 상품&회원)
       실제 테이블로 설계할 수는 없음 >> 왜냐하면 테이블은 정의할 때 컬럼이 고정되기 때문에 수평확장이 불가능하기 때문

    >> 하지만 테이블의 레코드 추가 즉 수직확장은 가능하기 때문에
       위 관계 사이를 연결할 Mapping Table을 생성해 양쪽에 필요한 정보를 끌어다 사용함으로서 문제를 해결함

    매핑테이블 작성
    >> 정보를 끌어다 쓸 기반 테이블이 먼저 존재해야함

    >> 보통 논리적으로 연결되는 기반테이블은 명사의 역할을 하고 매핑테이블은 그 사이 동사나 히스토리의 역할을 한다
       회원이 영화를 평가한다 -> 회원:영화 (명사) 사이 평가하다(동사)의 매핑 테이블이 낀다

    >> 매핑테이블은 중간에서 양쪽의 PK를 참조함

    JPA에서 M:N처리
    1. @ManyToMany
    일단 @ManyToMany가 존재하여 자동으로 매핑테이블 생성이 가능하지만

    직접 엔티티를 짜는 방식이 아니라 관계만을 자동으로 설정하기에 두 리뷰 데이터 같은 둘 사이 추가적인 데이터 기록이 안됨
    특히 이용되는 양방향참조는 매우 주의가 필요한 방식
    >> JPA의 실행상 가장 중요한 점은 메모리상(콘텍스트상)의 엔티티 객체들과 DB의 상태를 동기화 시키는 것이고,
       연관 객체가 많을수록 하나의 객체를 수정시 다른 객체 상태도 매번 일치시키는 상태를 정확히 유지하는 것이 어렵기 때문

    따라서 우선 2. 직접 별도의 엔티티를 설계하여 @ManyToOne으로 연결하여 작업한다.



 */