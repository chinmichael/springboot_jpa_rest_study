package com.springboot_ex.jpa_test.dto;

/* DTO : Data Transfer Object : 단순히 데이터를 담아 전달하는 일회성 컨테이너 (비교적 읽고 쓰는 허용범위가 Entity보다 넓다)
    >> Mybatis 등의 기존 연결 방식 때 쓰이던 것처럼 '각 계층간' 데이터 '전달'이 목적
    >> JPA Entity객체는 실제 DB와 관련된 Entity Manager에 관리되는 대상으로 보안성이나 Lifecycle이 DTO와 전혀 다름

    >> HttpServletRequest나 HttpServletResponse는 Controller계층에서만 움직이고 Service로 전달되지 않는 것처럼
       Entity객체도 JPA에서만 사용 계층 사이 데이터 전달은 DTO

    >> DTO를 사용함으로서 데이터 안전성 상승 + Entity객체 범위 한정 가능
       But
       Entity와 유사코드가 중복발생 + Entity<>DTO 변환과정이 필요
       (DTO클래스에 적용 or 책처럼 서비스 등에 따로 구현 or 라이브러리사용(ModelMapper나 MapStruct)
*/

import lombok.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;

//@Data DTO니까 @Setter가 들어가도 비교적 괜찮긴 하지만 되도록 setter는 필요에 따라 적절히 넣는 방안으로 처리 Builder초기화도 되고
@Builder
@NoArgsConstructor
@AllArgsConstructor // 생성자 처리는 필드 값에 따라 적절히 처리하도록한다
@Getter
@Setter // 우선 연습예제니까 넣음
public class GuestbookDTO {

    private Long gno;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate, modDate;

    public String toString() {
        String pattern = "GuestbookDTO(gno={0}, title={1}, content={2}, writer={3}, regDate={4}, modDate={5})";
        Object[] input = {this.gno, this.title, this.content, this.writer, this.regDate, this.modDate};
        String result = MessageFormat.format(pattern, input); // 최근에 자습한 MessageFormat 바로 써먹기
        return result;
    }
}
