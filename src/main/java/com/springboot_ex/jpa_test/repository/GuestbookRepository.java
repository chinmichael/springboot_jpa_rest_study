package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long>, QuerydslPredicateExecutor<Guestbook> {
}
/*
    JPA 쿼리메서드, @Query >> 선언시 고정 형태를 갖춤 (파라미터가 아닌 쿼리문 자체가 선언시 고정)
                            >> 복잡한 조합 + 많은 경우의 수로 인한 동적 쿼리 생성이 필요할 경우 (복잡한 검색조건과 조인, 서브쿼리 기능 필요시)
                            >> Querydsl : 코드 내부 상황에 맞게 동적으로 쿼리를 구성할 수 있음

    Querydsl >> 엔티티 클래스를 그대로 사용하는 것이 아닌 Querydsl 라이브러리를 통해 엔티티 클래스를 변환한 Q도메인 클래스를 활용
             >> build관리에서 plugin, dependency, task를 추가 후 compileQuerydsl task를 실행하여 build > generated폴더에 Q도메인 클래스를 생성한다
             >> 엔티티 클래스를 기반으로 라이브러리를 통해 자동 생성하기에,
                개발자는 Q도메인을 건드는게 아니라 엔티티클래스와 기존 Repository I/F에 추가로  QuerydslPredicateExecutor를 상속시킨 I/F를 작성한다.
                
             >> 주로 복잡한 select처리에서 사용
                예를 들어 검색조합이 다양할 경우 Guestbook에서 제목/내용/작성자의 검색이 단일~전체 검색 경우의 수를 갖게 되는 경우만해도 3 + 3*2/2 + 1 총 7경우의 수
                따라서 상황에 맞게 쿼리 조합이 이뤄져야할 때가 있다 (하기사 MyBatis에서 걍 매핑할때는 조합 갯수대로 쿼리를....우읍)
                
             >> 사용법 >> Test에서 어케 쓰는지 확인하자
                1. BooleanBuilder생성
                2. 조건에 맞는 구문은 Querydsl에서 사용하는 Predicate타입 함수 사용
                3. BooleanBuilder에 작성된 Predicate 추가 후 실행

 */