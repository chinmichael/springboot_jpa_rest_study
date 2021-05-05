package com.springboot_ex.jpa_test.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Guestbook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;
    
    //JPA 엔티티 클래스는 JPA관리의 복잡성을 줄이고 안전성을 높이기 위해 Setter메서드를 매우 지양하여야함
    //지양해야하나 수정확인 예제 연습을 위해 여기서만
    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeContent(String content) {
        this.content = content;
    }
}
