package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchBoardRepository {
    Board search1();
    Board search2();

    Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
}

/*  연관관계 설정된(ManyToOne 등) 상황에서의 검색 JPQL구성
    >> 여러 엔티티 타입을 처리하는 경우 Object[] 타입으로(Tuple타입이라 함) 나오기에 구성이 복잡 (대신에 어디든 써먹기 가능)
    
    밑준비
    >> 검색(조회)처리는 Querydsl로 동적으로 작성시키는 경우가 있으므로 compileQuerydsl task를 실행하여 Q도메인 클래스 생성
    
    >> 쿼리 메서드나 @Query로 처리할 수 없는 기능을 위한 I/F준비 (가능하면 필요한 기능을 명시 + 메서드 이름은 쿼리메서드와 구분)
    
    >> I/F구현 클래스 생성 (이때 반드시 I/F + Impl의 이름으로 작성 / Querydsl라이브러리를 통한 기능 직접 구현을 위해 QuerydslRepositorySupport 클래스 상속)
       (cf : 전에는 QuerydslPredicateExecutor<E>를 I/F에 상속시켜 자동구현시켰다)

    >> I/F 기능을 구현클래스에 Q도메인 클래스와 JPQLQuery를 이용해 구현

    >> 추가 기능의 레포지토리 I/F를 기존 엔티티 레포지토리 I/F에 상속시킨다.
    
    ※ QuerydslRepositorySupport : Spring Data JPA에 포함된 클래스 >> Querydsl라이브러리로 직접 무언가를 구현할 때 사용
    
 */
