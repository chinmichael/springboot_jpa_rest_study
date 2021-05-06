package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.GuestbookDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import com.springboot_ex.jpa_test.entity.Guestbook;
import com.springboot_ex.jpa_test.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service // 스프링에서 빈으로 처리되도록
@Log4j2 // 추가로 로그를 뽑아내기 위해(그리고 웹콘솔에도) 씀 >> 세세한건 다음에
@RequiredArgsConstructor // final 필드 생성자 생성 > 의존성 자동 주입을위해 사용 자세한건 하단 주석
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookRepository respository; // 위 생성자 어노테이션을 통한 자동주입처리 방식이므로 반드시 final 선어

    @Override
    public Long register(GuestbookDTO dto) {
        log.info("DTO --------------------------- ");
        log.info(dto);

        Guestbook entity = dtoToEntity(dto);

        log.info(entity);

        log.info("Before Test -------------------- ");
        //return null; // 우선 여기까지 작성해 테스트로 로그를 확인한 뒤 데이터 처리가 잘되면 실제 DB에 꽂을수 있도록 코드를 작성 후 재테스트

        respository.save(entity);

        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());
        
        Page<Guestbook> result = respository.findAll(pageable);
        
        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
        // 람다식을 사용해 I/F의 디폴트 변환 메서드를 사용하여 DTO변환처리하는 함수를 PageResultDTO로 보낼 수 있게 처리
        
        return new PageResultDTO<>(result, fn);
        // PageResultDTO<GuestbookDTO, Guestbook>이지만 답정너의 명확한 매개변수화된타입일 경우는 생략 가능
    }
}

/* Spring Container(서블릿, 루트, 개발자), Bean
   https://codevang.tistory.com/248
   https://codevang.tistory.com/258

   전제 : 스프링도 자바 서블릿 컨테이너 구동방식 위에서 구동되는 라이브러리 집합

   Servlet :
   서버에서 웹페이지 등을 동적으로 생성하거나(MVC) 데이터처리를 수행하기 위해(API) 자바로 작성된 프로그램
   자바언어로 웹 애플리케이션을 좀 더 쉽게 개발하기 위해 만든 API

   스프링 MVC 구동
   1-1. WAS(톰캣 : 서블릿 컨테이너라 대강 생각) 구동(web.xml)
    + 톰캣 server.xml의 path경로(일단 프로젝트명)를 달고 오는 url은 해당 프로젝트(App)에 대한 요청이란 의미
   1-2. 루트컨테이너 생성 : 루트컨테이너 설정은 구동시 참조하는 web.xml에 있음
    - 루트컨테이너 : App의 최상위 컨테이너 : 웹기술과 관련없는 자원을 Bean으로 만들어 관리
    - 관리를 위해 default로 제공하는 root-context.xml, 필요에따라 application context 추가

    - 서블릿, 루트 컨테이너는 스프링 구조에서 거의 자동적으로 생성 xml로 설정을 부분적으로 조정하는 정도임
    - 루트 컨테이너 : 모든 서블릿에서 공유할 전역설정와 Bean생성 관리

   1-3. DispatcherServlet : url매핑 (서블릿 컨테이너에서 클라이언트로부터 받은 url 요청을 어떤 서블릿 클래스로 넘길지)
                            servlet-context.xml에서 파라미터로 제공

   1-4. filter : 서블릿으로 요청이 들어가기전 + 최종 응답 전 공통으로 수행되어야 할 기능을 구현 (인코딩이나 스프링 시큐리티)

   --- 여기까지가 서블릿 컨테이너 구동 > 클라이언트 요청 대기상태

   2. 클라이언트 요청에 따른 서블릿 구동

   2-1. DispatcherServlet 로드 및 스프링 컨테이너 생성
    - 요청(url) 들어오면 서블릿 컨테이너가 매핑된 서블릿을 찾아 메모리 로드
    - 해당 서블릿 첫 구동시 초기화 파라미터로 servlet-context.xml의 파일을 넘겨 이 설정대로 서블릿 동작
    - 설정대로 루트 컨테이너에 자식인 서블릿컨텍스트(컨테이너이긴한데 WAS가 아님) 생성 > 구동에 필요한 핸들러, 컨트롤러 등의 Bean객체 생성

   2-2.servlet-context.xml설정대로 기능분배 in servlet context
    - 들어온 요청에 대해 매핑된 컨트롤러 클래스로 요청 전달
    - component-scan은 어노테이션 스캔할 패키지범위
    - @Bean으로만 Controller생성도 되지만 그만큼 위 설정 파라미터에 직접적으로 설정을 해야하므로 걍 @Controller가자(그리고 명시적으로 좋음)

    + css, js, 이미지 등 정적 리소스는 클라이언트가 직접 접속 가능한 폴더에 넣어 엑세스하도록 처리(default인 /resource에 없다면 DispatchServlet에 추가)

    + API가 아닌 MVC 프로젝트이므로 컴파일된 View Page코드는 메모리에 로드된 상태(WAS에 있음)로 재활용

    ----여기까지가 서블릿 구동에 필요한 컨테이너 이야기
    개발자가 짜는 컨테이너와 @Bean의 경우 POJO클래스 지향상 권고사항도 아니고 아직 좀 더 이해가 필요하므로 다음에 정리
    위에것도 우선 그냥 흐름만 생각
    https://codevang.tistory.com/248
*/

/*  @Service(서비스 레이어 : 로직처리), @Repository(퍼시스턴스 레이어 : DB등 외부 I/O처리) 어노테이션

    @Controller(프레젠테이션 레이어 : 요청응답처리) 어노테이션이 핸들러가 스캔가능한 Bean객체로서 서블릿 컨텍스트에 생성된다면
    (그냥 @Bean으로 만들기 쌉귀찮)

    @Service, @Repository는 그냥 루트 컨테이너에 Bean객체로 생성해주는 어노테이션 (루트에 생성하니 전 서블릿이 참조가능)
    @Component와 사실상 기능이 셋다 똑같다... 하지만 명시적으로 각 역할을 표현해주기 위해 따로 표현한 어노테이션이라 생각하면 됨
 */

/*  @RequiredArgsConstructor와 의존성 자동주입

    @RequiredArgsConstructor : 초기화되지 않은 final필드나 @NonNull 필드에 대한 생성자를 생성해준다
    +
    Spring 의존성 주입 특징 중
    어떤 Bean에 생성자가 하나만 있고 생성자 파라미터 타입이 Bean에 등록가능한 존재라면 @Autowired 없이 의존성 주입이 가능함

    >> 두개가 맞물려 final필드에 생성자 의존성 주입처리가 되는것

    왜 이런 처리를 할까?
    우선 의존성 주입 방법에는 3가지가 있음
    1. 생성자
    class Exam {
        private final S s;

        @Autowired
        public Exam(S s) { this.s = s; }
    
    2. Setter
    class Exam {
        private S s;
        
        @Autowired
        public void setS(S s) { this.s = s; }
    }
    
    3. 필드
    class Exam {
        @Autowired
        private S s;
    }
    
    보통 간결한 필드인젝션을 많이 취하는데 이 경우는 몇가지 문제를 내재한다.
    그 중 몇가지를 짚으면
    1. final 선언이 안되므로 불변성 설정이 안 됨
    2. 순환의존성 (A >> B >> C >> A 참조하는 얘들끼리 어느새 서로 참조하고 앉음), 단일책임 문제 등을 파악하기 힘듦
    3. DI컨테이너와 결합도가 높음 (DI컨테이너가 없음 안됨 POJO지향 특성상 아이러니하게 스프링에 너무 의존적이면 안 좋다)
    
    하지만 다른 두 방식은 의존성 주입 필드가 좀 많아지면 코드 복잡성이 올라가는데
    이때 @RequiredArgsConstructor를 선언하고 각 의존성 주입 객체에 final 선언을 처리하면 불변성 처리 + 간결히 정리됨
 */
