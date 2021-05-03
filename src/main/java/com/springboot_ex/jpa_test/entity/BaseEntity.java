package com.springboot_ex.jpa_test.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
abstract class BaseEntity {
    @CreatedDate
    @Column(name="regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;
}

/*  데이터의 등록시간 수정시간을 자동으로 처리할 수 있도록 작성한 추상 클래스

    @MappedSuperclass : Super란 말처럼 적용된 클래스로 테이블을 생성하지 않고 상속한 클래스로만 엔티티가 생성되게 함

    @EntityListener
    JPA의 영속콘텍스트(Persistence Context / JPA만의 고유 메모리공간) >> 엔티티객체들을 관리하기 위해 존재(대충 JDBC커넥터로 통신하기 전정도)
    > MyBatis의 경우 이 영속콘텍스트 단계가 없음
      : SQL을 위해 전달되는 객체는 SQL 실행을 위한 데이터컨테이너 느낌이라 SQL처리가 끝난 뒤는 어떻게 되던 상관이 없음

    > JPA의 경우는 엔티티 클래스의 변경사항이 DB에 반영되기에 영속콘텍스트에서 관리를 한다 (엔티티 유지 + 필요시 꺼내어 재사용)
    > 이런 엔티티 객체 변화를 감자하는 것이 Listener
    > 그리고 엔티티 객체의 생성과 변경을 감지하는 역할은 AuditingEntityListener가 맡는다

    @CreatedDate : JPA에서 엔티티 생성 시간을 처리 updatable 속성을 false로 하여 DB 반영시 regedate 값이 생성 후 변경되지 않도록 함
    @LastModifiedDate : 최종 수정시간을 자동으로 처리

 */

