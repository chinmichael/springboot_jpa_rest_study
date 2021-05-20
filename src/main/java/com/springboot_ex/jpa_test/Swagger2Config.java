package com.springboot_ex.jpa_test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*  Swagger : API 문서를 자동으로 생성해주는 라이브러리

    개발하는 API 서버의 SPEC 문서화 및 조회 및 관리는 중요하지만 매우 번거로운 작업이다
    이를 자동으로 생성해 HTML로 출력해주는 라이브러리이다
    +
    심지어 각 기능을 테스트하는 것도 가능하다

    생성해주는 API Document URL : http://<ip>:/<port>/<base>/swagger-ui.html

    Controller에 각 메서드에 설명을 추가해주는 어노테이션
    @ApiOperation(value="", notes="")

    DTO 각 필드에 설명을 추가해주는 어노테이션
    @ApiModelProperty(value="")
 */

@EnableSwagger2
// JPA와 마찬가지로 실행 main인 @SpringBootApplication에 설정해도 됨 | Swagger 기능 활성화
@Configuration
// @Bean 메소드를 제공하는 클래스 >> 스프링 컨테이너가 해당 Bean정의를 생성하고 런타임 시 해당 Bean 요청을 처리할 것을 선언시키게 함
// 간단히 말해 XML파일로 정의하던 bean정의를 java클래스로 할 수 있도록 말그대로 환경설정 클래스를 지정해주는 어노테이션
public class Swagger2Config {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.springboot_ex.jpa_test."))
                // 검색을 할 Controller의 범위 제한
                .paths(PathSelectors.any())
                // 검색된 Controller의 매핑 URL을 특정패턴으로 제한
                // .paths(PathSeleteors.ant("/api/**") : 정규식으로 url이 /api/**인 경우만 필터링
                .build();
    }

    private ApiInfo apiInfo() { // 부가적인 API 문서 커스텀

        return new ApiInfoBuilder()
                .title("JPA/API/TEST Exercise") // 문서 제목
                .version("1.0") // 문서 버전
                .description("") // 문서 설명
                .license("") // 라이센스
                .licenseUrl("") // 라이센스 url
                .build();
    }

}
