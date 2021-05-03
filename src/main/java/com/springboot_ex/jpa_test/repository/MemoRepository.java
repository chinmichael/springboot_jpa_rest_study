package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
Spring Data JPA는 인터페이스를 통해 관련 객체를 내부에서 자동생성(해당하는 코드들을)
>> 개발자가 인터페이스만 만들면됨
>> Repository > CrudRepository (일반적인 Crud 기능만) > PagingAndSortRepository > JpaRepository 로 상속구조 취함(젤 밑의 Jpa는 당근 모든 기능 챙김)
 */
public interface MemoRepository extends JpaRepository<Memo, Long> { // JpaRepository<엔티티클래스타입, Id데이터타입>으로 자동 bean 등록

    List<Memo> findByMnoBetweenOrderByMnoDesc (Long from, Long to);
    // mno필드를 기준으로 from and to 사이 값으로 select 정렬은 mno필드로 역순정렬
    
    Page<Memo> findByMnoBetween (Long from, Long to, Pageable pageable);
    // OrderBy 정렬조건을 쿼리메서드 이름으로 처리하면 너무 길어지므로 Pageable 파라미터를 이용해 정렬조건을 설정
    // Pageable 파라미터는 모든 쿼리메서드에 적용 가능

    void deleteMemoByMnoLessThan (Long num);
    // 메서드 실행시에는 @Transactional, @Commit 어노테이션이 필요함
    // @Transactional이 필요한 이유는 deleteBy의 경우 우선 select로 해당 엔티티객체들을 가져오는 작업이 삭제작업과 같이 이뤄지기 때문
        // ++ 각 엔티티 객체를 하나씩 삭제해나가기 때문에 실제 개발시에는 잘 쓰이지 않음 >>@Query를 쓰자
    // @Commit이 없으면 기본적으로 Rollback처리됨




}

/*  쿼리메서드, JPQL(Java Persistence Query Language 객체지향쿼리) > 주로 select기능 쪽에 검색조건 설정이 기존 메서드로는 힘드므로
    
    쿼리메서드 : 메서드 이름 자체가 쿼리구문으로 처리
    @Query : 엔티티 클래스 정보를 이용해 SQL과 유사한 쿼리를 작성하는 기술
             > nativeQuery옵션을 true로 하면 DBMS에 해당하는 순수 SQL문 작성 가능 (대신 DBMS에 상관없이 알아서 적용시켜주는 JPA 장점 날아감)
    Querydsl : 동적 쿼리 처리 기능         
*/

/*  쿼리메서드 : 메서드 이름 자체가 질의문 (repository 인터페이스에 메서드 추가)
             > findBy~ getBy~로 시작 + 필드 조건, And, Or, Between 같은 키워드로 메서드명 지정
             > 파라미터는 키워드에 따라 결정됨
             
             > 리턴타입은 상당히 자유로움
             > 주로 좀 주의해야할 사항 : 1.select 작업시 List타입이나 배열 사용 가능 2.파라미터에 Pagealbe이 들어갈 경우 무조건 Page<E>타입
 */

/*
    @Query : 조인이나 복잡한 조건의 처리의 경우에는 쿼리 메서드도 부족 (간단한 조건처리는 쿼리메서드 좀 되면 @Query로)

    @Query(value="") value에 JPQL을 입력
    JPQL은 SQL과 유사하며 SQL에 사용하는 함수들도 동일 적용
    단지 ORM 특성상 / 테이블 대신 엔티티클래스 / 컬럼 대신 클래스 멤버변수(필드) 사용
    ex) select m from Memo m order by m.mno desc

    @Query 특징
    필요한 데이터만 선별적으로 추출 가능
    필요한 경우 각 DBMS에 맞게 Native SQL 사용 가능
    DML(insert/select/update/delete) 처리 가능

    메서드 파라미터를 받는 경우
    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :m.memoText where m.mno = :mno")
    int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText)

    파라미터 받는 방식
    ?1, ?2 등으로 1부터 시작하는 파라미터 순서
    :파라미터 를 이용하는 방식(위와 같음)
    #{}와 같이 자바 빈 스타일을 이용하는 방식 (Mybatis 생각 그럼 ${}을 써서 타입 넣기도 되려나 근데 그건 또 변수 이용해서 되기도 할듯)

    #{}로 객체를 받아 사용하는 경우 (파라미터를 간소화할 수 있음)
    @Transactional
    @Modifying (insert, update, delete의 DML을 사용하기 위해 붙여야 함)
    @Query("update Memo m set m.memoText = #{memo.memoText] where m.mno = #{memo.mno}")
    int updateMemoText(@Param("memo") Memo memo)

    @Modifying의 Attribute는 clearAutomatically, flushAutomatically
    @Query메서드를 통한 update, delete의 벌크연산(다건의 처리를 하나의 쿼리로 함, 영속성 컨텍스트 무시) 수행할 때 씀
    clearAutomatically는 메서드 실행 직후 영속성 컨텍스트를 clear할것인지 체크하는거 default인 false로 하면 1차 캐시 문제 발생 가능
    JPA는 영속성 컨텍스트의 1차캐시(@Id를 키로 엔티티관리)를 통해 엔티티를 캐싱하고 DB접근횟수 줄임 벌크 연산시 이를 무시하고 처리해서 반영이 안되 싱크가 안 맞을 수 있음

    페이징처리, 정렬
    페이징처리 및 정렬의 경우 마찬가지로 파라미터에 Pageable타입의 파라미터를 받아서 처리하면 됨
    리턴타입이 Page<E>가 되야하는데 이에 count를 처리하는 쿼리는 countQuery 속성으로 지정이 가능(nativeQuery는 필수라는데... 얘는 없어도 알아서 해줄지도...)

    @Query(value = "select m from Memo m where m.mno > :mno",
            countQuery = "select count(m) from memo m where m.mno > :mno")
    Page<Memo> getListWithQuery(Long mno, Pageable pageable)

    데이터 선별 추출
    쿼리메서드의 경우 엔티티 객체 타입의 데이터로 추출 (List<Entity>나 Page<Entity> 등)
    @Query의 경우는 필요 데이터만을 추출 가능 + JOIN, GROUP BY나 CURRENT_TIMESTAMP등을 쓸 때 이런 필드가 있는 적절한 엔티티 타입이 없어도 처리 가능
    >> Object[] 타입을 리턴 타입으로 지정 가능

    JPQL으 시간 구문(CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP) + Object[] 리턴
    @Query(value = "select m.mno, m.memoText, CURRENT_DATE from Memo m where m.mno > :mno",
            countQuery = "select count(m) from memo m where m.mno > :mno")
    Page<Object[]> getListWithQueryObject(Long mno, Pageable pageable)
    
    
    Native SQL
    복잡한 JOIN 구문 처리등을 위해 어쩔 수 없는 경우 >> @Query(value="~", nativeQuery=true)로 설정 시 DBMS고유 SQL 작성 가능
 */