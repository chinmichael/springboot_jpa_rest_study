package com.springboot_ex.jpa_test.repository;

import com.springboot_ex.jpa_test.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 페이징 + 영화목록 + 영화대표 이미지 + 영화별 평균 점수 + 리뷰 개수
    @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r)" +
            " from Movie m" +
            " left join MovieImage mi on mi.movie = m and mi.inum = (select max(mi2.inum) from MovieImage mi2 where mi2.movie = m)" +
            " left join Review r on r.movie = m group by m")
    Page<Object[]> getListPage(Pageable pageable);

    /*  Memo
        1.coalesce(expression1, expression2) : 변수(expression1)의 값이 비었을 때 null이 아닌 지정된 값(expression2)를 반환함
        >> 이 경우 아직 평점이 매겨지지 않은 영화의 경우 0을 출력시켜야하기에

        2.만약 영화 이미지 중 가장 마지막에 등록된 것을 찾기 위해
        group by로 묶인 것 중 가장 큰 영화이미지번호의 이미지 개념으로 mi가 아닌 max(mi)로 찾는다고 하면 N+1 문제가 생긴다

        N+1문제
        1번의 쿼리로 N개의 데이터를 가져오게 될 때,
        N개의 데이터 처리를 위해 필요한 추가적인 쿼리가 각 N개에 대해 수행되는 경우

        이 경우 max()를 쓰지 않은 경우 join으로 처리할 때 가장 먼저 엑세스된(가장 번호의 레코드가 아님 / 수정 삭제가 발생하면 모르게됨)
        레코드의 데이터를 가져오지만

        max()과 같은 그룹함수를 쓸 때 count()로 단순 레코드 수를 센다거나 max(mi.inum)으로 해당 컬럼의 것 중 가장 큰 것을 가져오게 하는 경우 문제가 없지만
        해당 레코드 전체를 가져와야 할 경우 max(mi)를 쓰게 되면
        그룹으로 묶인 mi에서 pk인 inum을 기준으로 잘 최대값이 뽑힌다 쳐도 이제 다시 이를 기준으로 해당 레코드를 다시 뽑아와야 하기 때문에
        select문을 또 실행되며 이것이 첫번째 select문으로 조회된 각 영화 레코드마다 각각 select가 일어나기 때문에 페이징 크기의 10번만큼 N+1문제가 발생하는 것

        만약 inum이 가장 뒤인 경우를 조회하려면
        select m, mi, avg(coalesce(r.grade,0) count(distinct r)
        from Movie m
        left join MovieImage im on im.movie = m and im.inum = (select max(im2.inum) from MovieImage im2 where im2.movie = m)
        left join Review r on r.movie = m
        group by m

        책에서는 이렇게 서브쿼리를 조인 조건 안에 넣는 것으로 각 영화번호 케이스에 해당하는 이미지 번호가 가장 뒤의 것으로 제한 조건을 걸었는데...
        성능상 과연 그냥 max(im)과 얼마나 차이나냐란 느낌이긴 하지만 일단 적어도 max(im2.inum)으로 컬럼을 세팅하므로서 정확도는 높아보인다...

        아무튼 예전에 본 어떤 글에서는 where절 서브쿼리로 인한 성능하락을 최소화하기 위해 주목되는 join이지만
        오히려 join처리시 optimizer가 제대로 판단을 못하는 경우 join대상 레코드 수를 최소화하기 위해 역설적으로 서브쿼리가 유용하다는 거였는데
        이 경우도 조금은 그쪽에 속하려나...
     */

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r) " +
            "from Movie m " +
            "left join MovieImage mi on mi.movie = m " +
            "left join Review r on r.movie = m " +
            "where m.mno = :mno " +
            "group by mi")
    List<Object[]> getMovieWithAll(Long mno);

    @Query("select m, avg(coalesce(r.grade, 0)), count(r) " +
            "from Movie m " +
            "left join Review r on r.movie = m " +
            "where m.mno = :mno " +
            "group by m")
    List<Object[]> getMovieWithReview(Long mno);

    /*  쿼리 1단계
        select m, mi from Movie m left join MovieImage mi on mi.movie = m where m.mno = :mno
        >> 테스트를 해보면 레코드 결과는 해당 영화에 대한 이미지 개수만큼 나온다는것을 알 수 있다.

        거기에 리뷰 평균점수와 개수를 구하기 위해 group by로 묶어줄 때
        순간 조회 대상의 베이스가 Movie이므로 group by m이라고 생각할 수가 있다

        하지만 MovieImage를 조인하지 않고 Movie와 Review만을 조인했을 때 group by를 했을 때 비해
        위에서 m로 group by를 하면 이미지 개수의 곱만큼 튄 count값을 확인 가능

        >> 이는 group by가 출력되는 레코드를 무엇으로 묶을지 보는 것이기 때문에 당근 mi로 묶어야 정상처리가 된다

        의문점
        근데 레코드에서 이미지 파트를 제외한 나머지는 계속 중복이 되고 join이 두번 들어가는데
        만약 중복 조회되는 컬럼 수가 많아지면
        우선 영화와 리뷰만 조인으로 조회처리를 하고 이미지는 해당 영화번호에 대해 이미지 테이블에서 따로 조회를 하는 편 중 어느쪽이 나을까
     */
}
