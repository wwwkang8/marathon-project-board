package com.marathon.board.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.marathon.board.domain.Hashtag;
import com.marathon.board.dto.HashtagDto;
import com.marathon.board.repository.HashtagRepository;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public HashtagService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    /** 해시태그의 #을 제외한 키워드만 추출하여 파싱하는 로직 */
    public Set<String> parseHashtagNames(String content) {

        // 입력된 해시태그가 없는 경우 빈 Set을 리턴
        if(content == null) {
            return Set.of();
        }

        /**
         * 정규표현식 Pattern : 정규 표현식이 컴파일된 클래스. 정규 표현식에 대상 문자열을 검증하거나, 활용하기 위해 사용되는 클래스이다.
         * # : 문자열이 '#'으로 시작해야 함.
         * [ ] : 문자 클래스를 나타낸다. 이 안에 있는 문자들 중 하나와 매치해야 함.
         * \w : 알파벳 대소문자, 숫자, 밑줄에 해당하는 모든 "단어"문자와 매치됩니다
         * 가-힣 : 한글 유니코드 범위 . 이글에 속하는 모든 한글 문자와 매치된다.
         * + : 바로 앞에 있는 패턴이 하나 이상 포함되어야 함.
         *
         * java strip() : 문자열의 앞 뒤에 있는 공백을 제거한다(스페이스, 탭 등등)
         *
         * Matcher : 	패턴에 매칭할 문자열을 입력해 Matcher를 생성
         * */
        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        while(matcher.find()) {
            result.add(matcher.group().replace("#","")); // DB에 저장되는 값은 #이 빠진 값이 들어간다.
        }

        return Set.copyOf(result);
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    public void deleteHashtagWithoutArticles(Long hashtagId) {

        /**다른 글에 있는 해시태그가 없는지까지 확인해야 비로소 그 해시태그를 삭제할 수 있다
        여기에서는 해시태그가 정말 안 쓰이는지 검증하는 로직 필요.*/
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);

        // 해시태그에 딸려 있는 게시글이 있는지 확인한다.
        if(hashtag.getArticles().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }




    }
}
