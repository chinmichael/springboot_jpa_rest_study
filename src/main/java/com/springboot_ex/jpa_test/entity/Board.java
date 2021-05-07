package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString (exclude="writer") // ToString 어노테이션 처리시 참조하는 객체를 제외시키는 편이 좋음 (안 그럼 조회시 해당 객체의 toString이 필요하고 >> 결국 그쪽 테이블의 DB연결도 필요
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String content;

    @ManyToOne // 다대일 연관관계시 사용하는 참조 어노테이션 | 데이터를 가져와 저장해야 하므로 당연히 참조하는 측에 선언해야한다
    private Member writer;
    // 주의해야할 점은 SQL처럼 참조하는 PK 필드를 FK로 선언하는게 아니라 JPA에서는 그 Entity 객체를 참조한다 (물론 내부적으로는 알아서 참조대상 PK를 FK로 삼는다)
}

/*  RDBMS 관계 > 객체지향 JPA 처리 (M:1 다대일 연관관계 @ManyToOne)

    Tip : RDBMS에서 1:1 / M:1 / M : N 참조관계 구성(by PK / FK)는 참조되는 PK입장에서 판단하는 편이 수월하다
        : 예를 들어 멤버 한명이(PK) 여러 게시글 작성 가능(해당 PK참조 FK가 여러개) >> 다대일

        : RDBMS는 PK > FK의 단방향 참조 구조를 지님 | RDBMS에서 양방향 참조라는 개념은 없음
        : 객체지향 관점에서 관계처리를 하면 복잡해지는 경우가 많아 우선 RDBMS 모델링을 근간으로 시작하는 편이 좋다.
 */
