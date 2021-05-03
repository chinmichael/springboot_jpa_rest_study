package com.springboot_ex.jpa_test.entity;

/*
JPA(Java Persistence API) : Java를 통해 DB등 영속계층을 처리하기 위한 스펙

ORM(Object Relation Mapping) : 말 그대로 객체지향 패러다임(이에 근거한 언어:JAVA등)을 관계형 패러다임(RDBMS)에 매핑하는 개념
                             : 둘의 유사점 이용(클래스-테이블/인스턴스-레코드(VO객체와 각 인스턴스 생각)/참조-관계)

JPA는 ORM을 JAVA언어에 맞게 사용하는 스펙
스펙이기에 구현하는 구현체마다 프레임워크 이름이 다름 (보통 여기서도 쓰는건 Hibernate 오픈소스♡)

Spring Data JPA <> Hibernate <> JDBC <> DB

SpringBoot에서 Repository는 Mybatis때도 그랬지만 Spring Data JPA에서 제공하는 인터페이스로 설계 > 내부에서 자동 객체 생성 및 실행
                                                                         (개발자는 인터페이스 정의 작업만 하면 됨)

자동으로 테이블 생성하거나 JPA가 생성하는 SQL을 살피기 위해서는 application.properties 추가해야함
spring.jpa.hibernate.ddl-auto=update (자동으로 DDL실행하게 함) create를 쓰면 매번 생성시도를 하므로 update를 통해 필요에따른 alter나 create 실행
spring.jpa.properties.hibernate.format_sql=true 발생 sql 가독성 높임
spring.jpa.show-sql=true



 */

import lombok.*;

import javax.persistence.*;

//@Data는 @Setter도 내장되어 무분별한 Setter은 객체안전성이 떨어져 무조건적인 lombok Data는 지양


@Entity //JPA를 통해 관리하는 Entity클래스임을 선언
@Table(name="tbl_memo") //테이블이 없을 경우 JPA가 자동으로 DB설정과 테이블을 같이 확인하기에 에러표시가 날 수 있음 / 테이블뿐 아니라 인덱스 등도 생성 설정 가능
@ToString
@Getter
@Builder //객체 생성처리 Builder 디자인패턴을 구현한 것 > 복합객체 생성과 표현을 분리해 동일 생성절차에서 다른 표현결과 갖게 한것
//다양한 파라미터 생성자 가능성에서 Builder클래스를 이용해 필요한 파라미터를 넘겨받을 메서드 처리 후 build()하여 객체를 넘겨받아 생성
//https://github.com/greekZorba/java-design-patterns/tree/master/builder
//Builder에서 객체생성시 필요한 파라미터를 각각의 메서드로 받고 build메서드를 통해 최종적으로 객체를 만들어 넘긴다
//Builder패턴 사용이유 : 1.객체생성시 파라미터가 각각 다름 2.무조건적인 setter방지>불변객체 생성 가능 3.필수 파라미터 지정가능
@AllArgsConstructor
//Builder필수조건1 확장성을 위해 유용 하지만 모든 매개변수 가능성에 대한 생성자 생성으로 이미 생성전략이 있는 @Id에 해당하는 멤버도 넘겨받을 위험성 있음
//매개변수로 전략이 있는 PK를 받을 위험성을 줄이기 위해 넘겨받을 매개변수를 정한 생성자 메서드에 @Builder를 붙이는게 안정적 
@NoArgsConstructor //Builder필수조건2 JPA에서 프록시 생성을 위해 기본 생성자 필요
public class Memo {

    @Id // Entity 클래스에서 반드시 지정해야하는 PK에 해당하는 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK가 사용자 데이터 영역이 아닐 경우 사용하는 PK 생성전략
    // 1.AUTO(디폴트) JPA가 결정 2.IDENTITY DB가 결정(MYSQL, Mariadb는 AutoIncrement 3.SEQUENCE 4.TABLE 키 생성 전용 테이블 생성(@TableGenerator랑 같이)
    //@NoArgsConstructor(access = AccessLevel.PROTECTED)
    // 만약 UUID 등 사용자 정의 PK전략이 있다면 protected 세팅 후 @Builder를 적용하는 public 생성자를 따로 설정한다 안전성 상승(pk null 피함)
    private Long mno;

                                            // default값 지정을 위해서는 보통 columnDefinition속성 이용
    @Column(length = 200, nullable = false) // 필드(칼럼) 정의를 위함 (물론 제약조건 속성을 지정 가능 nullable, length, name 등)
    private String memoText;

    // 혹은 위처럼 빈생성자를 protected를 하지 않는 경우 Builder메서드 이름을 부여하여 (@Builder(builderMethodName="memoBuilder")
    // 다음과 같은 메서드로 PK전략이 커스텀일때 반드시 입력을 처리하게 할 수도 있다
    /*
    public static MemoBuilder builder(Long mno) {
        if(mno == null) {
            throw new IllegalArgumentException("PK누락");
        }
        return memoBuilder().mno(mno);
     }

     */


}
