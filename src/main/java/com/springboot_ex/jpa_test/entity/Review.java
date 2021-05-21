package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"movie","movieMember"}) // 두 테이블을 FK로 연결하므로
public class Review extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewnum;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private Movie movie;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private MovieMember movieMember;
    
    private int grade;
    
    private String text;
    
    // 실제로 위와 같은 단순 연결이 아닌 데이터를 기록하기 위해서는 @ManyToMany가 아닌 @ManyToOne으로 연결한 매핑용 엔티티클래스를 활용
}

