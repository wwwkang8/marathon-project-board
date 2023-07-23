package com.marathon.board.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.marathon.board.domain.Hashtag;
import com.marathon.board.repository.HashtagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("비즈니스 로직 - 해시태그")
@ExtendWith(MockitoExtension.class) /** 클래스에 해당 어노테이션을 달, 클래스가 Mockito를 사용함을 명시적으로 알림 */
public class HashtagServiceTest {
    /**
     * Junit5 : java 기반의 테스트를 도와주는 프레임워크. Mockito 없이 사용 가능
     * Mockito : Mocking을 해서 가짜 객체를 만들어서 테스트를 도와주는 프레임워크
     * */

    /** @Mock이 붙은 객체를 @InjectMocks가 붙은 객체에 주입한다
     *  HashtagService는 HashtagRepository를 주입받아야 하기 때문에 @InjectMocks 사용.
     * */
    @InjectMocks private HashtagService sut;

    @Mock private HashtagRepository hashtagRepository;

    /**
     * @ParameterizedTest : 같은 함수에 대해서 아규먼트를 바꿔서 여러번의 테스트를 실행할 때 사용한다.
     * 참고문서 : https://www.baeldung.com/parameterized-tests-junit-5#6-method
     * @MethodSource : name을 지정하면
     * */

    @DisplayName("본문을 파싱하면, 해시태그 이름들을 중복없이 반환한다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
    void givenContent_whenParsing_thenReturnsUniqueHashtagNames(String input, Set<String> expected) {
        //Given


        //When
        Set<String> actual = sut.parseHashtagNames(input);


        //Then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        then(hashtagRepository).shouldHaveNoInteractions();

    }

    static Stream<Arguments> givenContent_whenParsing_thenReturnsUniqueHashtagNames() {
        return Stream.of(
            arguments("#java", Set.of("java")),
            arguments("#", Set.of()),
            arguments("#   ", Set.of()),
            arguments("   #", Set.of()),
            arguments("#", Set.of()),
            arguments("", Set.of()),
            arguments("    ", Set.of()),
            arguments("#java_spring", Set.of("java_spring")),
            arguments("#java#spring", Set.of("java", "spring")),
            arguments("#java__#spring", Set.of("java__", "spring")),
            arguments("__#java#__spring", Set.of("java", "__spring")),
            arguments("#java#spring#부트", Set.of("java", "spring", "부트")),
            arguments("#java #spring#부트", Set.of("java", "spring", "부트")),
            arguments("#java#spring #부트", Set.of("java", "spring", "부트")),
            arguments("#java,#spring,#부트", Set.of("java", "spring", "부트")),
            arguments("#java.#spring;#부트", Set.of("java", "spring", "부트")),
            arguments("#java|#spring:#부트", Set.of("java", "spring", "부트")),
            arguments("#java #spring  #부트", Set.of("java", "spring", "부트")),
            arguments("   #java,? #spring  ...  #부트 ", Set.of("java", "spring", "부트")),
            arguments("#java#java#spring#부트", Set.of("java", "spring", "부트")),
            arguments("#java#java#java#spring#부트", Set.of("java", "spring", "부트")),
            arguments("#java#spring#java#부트#java", Set.of("java", "spring", "부트")),
            arguments("#java#스프링 아주 긴 글~~~~~~~~~~~~~~~~~~~~~", Set.of("java", "스프링")),
            arguments("아주 긴 글~~~~~~~~~~~~~~~~~~~~~#java#스프링", Set.of("java", "스프링")),
            arguments("아주 긴 글~~~~~~#java#스프링~~~~~~~~~~~~~~~", Set.of("java", "스프링")),
            arguments("아주 긴 글~~~~~~#java~~~~~~~#스프링~~~~~~~~", Set.of("java", "스프링"))
        );
    }

    @DisplayName("해시태그 이름들을 입력하면, 저장된 해시태그 중 이름에 매칭하는 것들을 중복 없이 반환한다.")
    @Test
    void givenHashtagNames_whenFindingHashtags_thenReturnsHashtagSet() {
        // Given
        Set<String> hashtagNames = Set.of("java", "spring", "boots");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames)).willReturn(List.of(
            Hashtag.of("java"),
            Hashtag.of("spring")
        ));

        //When
        Set<Hashtag> hashtags = sut.findHashtagsByNames(hashtagNames);

        //Then
        assertThat(hashtags).hasSize(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);

    }



}
