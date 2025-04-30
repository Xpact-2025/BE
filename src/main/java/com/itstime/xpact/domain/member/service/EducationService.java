package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.entity.Education;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.EducationRepository;
import com.itstime.xpact.domain.member.repository.SchoolCustomRepositoryImpl;
import com.itstime.xpact.domain.member.util.TrieUtil;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EducationService {

    private final SecurityProvider securityProvider;
    private final TrieUtil trieUtil;

    private final SchoolCustomRepositoryImpl schoolCustomRepository;
    private final EducationRepository educationRepository;


    // 학교 전체 조회
    @Transactional(readOnly = true)
    public List<String> readSchoolNames() throws CustomException {

        securityProvider.getCurrentMemberId();

        return schoolCustomRepository.findAllSchoolNames();
    }

    // 학교 이름 검색함으로써 조회
    @Transactional(readOnly = true)
    public List<String> autocompleteName(String term) throws CustomException {

        try {
            securityProvider.getCurrentMemberId();

            // Trie의 keyword에 검색어 넣기
            trieUtil.addAutocompleteKeyword(term);

            // DB에 있는 School name 전체 load
            List<String> schoolNames = schoolCustomRepository.findAllSchoolNames();
            trieUtil.loadDatasIntoTrie(schoolNames);

            // prefix와 일치 반환
            return trieUtil.autocomplete(term);
        } catch (Exception e) {
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Trie 구조 다시 비우기
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 학교를 기반으로 학과 필터링 ( 전체 조회 )
    @Transactional(readOnly = true)
    public List<String> readMajor(String schoolName) throws CustomException {

        securityProvider.getCurrentMemberId();

        return schoolCustomRepository.findMajorBySchoolName(schoolName);
    }

    // 직접 학과 검색
    @Transactional(readOnly = true)
    public List<String> searchMajor(String schoolName, String term) throws CustomException {

        try {
            securityProvider.getCurrentMemberId();

            // Trie의 keyword에 검색어 넣기
            trieUtil.addAutocompleteKeyword(term);

            // DB에 있는 School name 전체 load
            List<String> majorNames = schoolCustomRepository.findMajorBySchoolName(schoolName);
            trieUtil.loadDatasIntoTrie(majorNames);

            // prefix와 일치 반환
            return trieUtil.autocomplete(term);
        } catch (Exception e) {
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Trie 구조 다시 비우기
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 학력사항 저장 ( 선택을 한 것을 파싱하든, 직접 입력하든 )
    @Transactional
    public EducationSaveResponseDto saveEducationInfo(EducationSaveRequestDto requestDto) {

        Member member = securityProvider.getCurrentMember();

        // 학교명과 학과명 입력하기
        String educationName = createEducation(requestDto);

        Education education = Education.builder()
                .member(member)
                .schoolName(requestDto.name())
                .major(requestDto.major())
                .schoolStatus(requestDto.schoolStatus())
                .educationName(educationName)
                .startedAt(requestDto.startedAt())
                .endedAt(requestDto.endedAt())
                .build();

        member.setEducation(education); // 최종만 저장
        educationRepository.save(education);

        return EducationSaveResponseDto.toDto(education);
    }

    private String createEducation(EducationSaveRequestDto requestDto) {

        String statusName = requestDto.schoolStatus().getDisplayName();
        return String.format("%s %s (%s)",
                requestDto.name(),
                requestDto.major(),
                statusName);
    }

}
