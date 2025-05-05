package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.entity.Education;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.EducationRepository;
import com.itstime.xpact.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;


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

            // DB에 있는 해당 schoolName의 학과명 전체 load
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

        Long memberId = securityProvider.getCurrentMemberId();

        // 실제 DB에서 영속 상태의 Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXISTS));


        // 학교명과 학과명 입력하기
        String educationName = createEducationName(requestDto);

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

    @Transactional
    public EducationSaveResponseDto updateEducationInfo(EducationSaveRequestDto requestDto) {

        Long memberId = securityProvider.getCurrentMemberId();

        // 실제 DB에서 영속 상태의 Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXISTS));

        Education education = educationRepository.findByMemberId(member.getId())
                .orElseThrow(() -> CustomException.of(ErrorCode.EDUCATION_NOT_FOUND));

        // 값이 존재할 경우에만 업데이트
        if (requestDto.name() != null) {
            education.setSchoolName(requestDto.name());
        }

        if (requestDto.major() != null) {
            education.setMajor(requestDto.major());
        }

        if (requestDto.schoolStatus() != null) {
            education.setSchoolStatus(requestDto.schoolStatus());
        }

        if (requestDto.startedAt() != null) {
            education.setStartedAt(requestDto.startedAt());
        }

        if (requestDto.endedAt() != null) {
            education.setEndedAt(requestDto.endedAt());
        }

        // 학교명 또는 전공이 변경되었으면 educationName 재생성
        if (requestDto.name() != null || requestDto.major() != null) {
            String educationName = createEducationName(EducationSaveRequestDto.of(
                    requestDto.name() != null ? requestDto.name() : education.getSchoolName(),
                    requestDto.major() != null ? requestDto.major() : education.getMajor(),
                    requestDto.schoolStatus() != null ? requestDto.schoolStatus() : education.getSchoolStatus()
            ));
            education.setEducationName(educationName);
        }

        return EducationSaveResponseDto.toDto(education);
    }

    private String createEducationName(EducationSaveRequestDto requestDto) {

        if (requestDto.schoolStatus() == null) {
            throw new CustomException(ErrorCode.EMPTY_SCHOOL_STATUS);
        }

        String statusName = requestDto.schoolStatus().getDisplayName();
        return String.format("%s %s (%s)",
                requestDto.name(),
                requestDto.major(),
                statusName);
    }

}
