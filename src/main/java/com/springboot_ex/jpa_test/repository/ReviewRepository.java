package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import com.springboot_ex.jpa_test.entity.MovieMember;
import com.springboot_ex.jpa_test.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"movieMember"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Review> findByMovie(Movie movie);

    /*  @EntityGraph : Fetch방식이 LAZY일 때 지정한 FK컬럼의 엔티티를 함께 로딩하도록 (쿼리를 보면 자동 join처리됨) 해주는 어노테이션
        >> 특정 기능을(메서드를) 수행할 때만 기본 LAZY모드에서 EAGER모드 적용을 시키는게 가능
        >> @Query의 조인처리를 대신 해줄 수 있음

        @EntityGraph 속성
        attributePaths = 로딩 설정을 변경하고 싶은 속성 (배열로 명시)

        type = 어떤 방식으로 적용할 것인지 (FETCH : 명시한 것만 EAGER 나머진 LAZY / LOAD : 명시한건 EAGER 나머진 엔티티 클래스에 명시되거나 기본 방식)
     */

    @Modifying
    @Query("delete from Review r where r.movieMember = :movieMember")
    void deleteByMovieMember(MovieMember movieMember);
    
    /*  쿼리메서드를 사용하지 않고 굳이 @Query를 이용한 이유는
        만약 위 해당하는 레코드가 여러개라고 할 때 각각의 레코드를 지우는 예상치 못하는 결과가 나올 수 있다.
        
        따라서 @Query를 이용하여 where절을 지정함으로서 비효율을 막을 수 있음
     */
}
