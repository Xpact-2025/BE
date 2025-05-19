package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.common.Degree;
import com.itstime.xpact.domain.member.common.SchoolStatus;
import com.itstime.xpact.domain.member.dto.request.EducationSaveRequestDto;
import com.itstime.xpact.domain.member.dto.response.EducationSaveResponseDto;
import com.itstime.xpact.domain.member.entity.Education;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.EducationRepository;
import com.itstime.xpact.domain.member.repository.SchoolCustomRepositoryImpl;
import com.itstime.xpact.domain.member.util.TrieUtil;
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

    private final TrieUtil trieUtil;

    private final SchoolCustomRepositoryImpl schoolCustomRepository;
    private final EducationRepository educationRepository;

    // 학교 전체 조회
    @Transactional(readOnly = true)
    public List<String> readSchoolNames() throws CustomException {

        return schoolCustomRepository.findAllSchoolNames();
    }

    // 학교 이름 검색함으로써 조회
    @Transactional(readOnly = true)
    public List<String> autocompleteName(String term) throws CustomException {

        try {
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

        return schoolCustomRepository.findMajorBySchoolName(schoolName);
    }

    // 직접 학과 검색
    @Transactional(readOnly = true)
    public List<String> searchMajor(String schoolName, String term) throws CustomException {

        try {
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
    public EducationSaveResponseDto saveEducationInfo(
            Member member,
            EducationSaveRequestDto requestDto) {

        Education education = educationRepository.findByMemberId(member.getId())
                .orElse(null);
        String educationName = createEducationName(education, requestDto);

        // Education 생성과 수정 로직 분리
        if (education == null) {

            education = createEducation(member, requestDto, educationName);
            member.setEducation(education);

            // Education DB 저장 반영
            educationRepository.save(education);

            return EducationSaveResponseDto.toDto(education);
        } else {
            modifyEducation(member, requestDto, educationName);

            return EducationSaveResponseDto.toDto(education);
        }
    }

    // Education을 새로 저장
    private Education createEducation(
            Member member, EducationSaveRequestDto requestDto, String educationName) {
        return Education.builder()
                .member(member)
                .degree(requestDto.degree())
                .schoolName(requestDto.name())
                .major(requestDto.major())
                .schoolStatus(requestDto.schoolStatus())
                .educationName(educationName)
                .build();
    }

    // 기존의 Education을 수정
    private void modifyEducation(
            Member member, EducationSaveRequestDto requestDto, String educationName) {
        member.getEducation().updateEducation(requestDto);
        member.getEducation().setEducationName(educationName);
    }


    private String createEducationName(Education education, EducationSaveRequestDto requestDto) {

        // Null에 대한 처리
        if (education == null && (requestDto.name() == null || requestDto.major() == null || requestDto.schoolStatus() == null)) {
            throw CustomException.of(ErrorCode.INSUFFICIENT_SCHOOL_INFO);
        }

        String name = requestDto.name() != null ? requestDto.name() : education != null ? education.getSchoolName() : null;
        String major = requestDto.major() != null ? requestDto.major() : education != null ? education.getMajor() : null;
        SchoolStatus schoolStatus = requestDto.schoolStatus() != null
                ? requestDto.schoolStatus()
                : education != null ? education.getSchoolStatus() : null;


        // 고등학교일 경우 로직 추가
        if ((education != null && Degree.HIGH.equals(education.getDegree())) ||
        (requestDto.degree() != null && requestDto.degree().equals(Degree.HIGH))) {
            major = "";
        }

        String statusName = schoolStatus.getDisplayName();
        return String.format("%s %s (%s)",
                name, major, statusName);
    }
}
