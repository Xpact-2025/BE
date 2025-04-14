package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.dto.request.SchoolSaveRequestDto;
import com.itstime.xpact.domain.member.entity.Education;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.entity.School;
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
public class SchoolService {

    private final SecurityProvider securityProvider;
    private final TrieUtil trieUtil;
    private final SchoolCustomRepositoryImpl schoolRepository;
    private final EducationRepository educationRepository;

    @Transactional(readOnly = true)
    public List<String> autocompleteName(String term) throws CustomException {

        try {
            securityProvider.getCurrentMemberId();

            trieUtil.addAutocompleteKeyword(term);

            // DB에 있는 School의 name 저장
            List<String> schoolNames = schoolRepository.findAllSchoolNames();
            trieUtil.loadDatasIntoTrie(schoolNames);

            return trieUtil.autocomplete(term);
        } catch (CustomException e) {
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Trie 비우기
            trieUtil.deleteAutocompleteKeyword(term);
        }
    }

    // 학교를 기반으로 학과 필터링하기
    @Transactional(readOnly = true)
    public List<String> searchMajor(String schoolName) throws CustomException {

        securityProvider.getCurrentMemberId();

        return schoolRepository.findMajorBySchoolName(schoolName);
    }

    @Transactional
    public String saveSchoolInfo(SchoolSaveRequestDto requestDto) throws CustomException {

        Member member = securityProvider.getCurrentMember();

        // 학교명과 학과명 입력
        School school = schoolRepository.findBySchoolNameAndMajor(
                    requestDto.name(), requestDto.major())
                    .orElseGet(() -> saveNewSchool(requestDto));

        String educationName = createEducation(requestDto);

        Education education = Education.builder()
                    .member(member)
                    .school(school)
                    .schoolStatus(requestDto.schoolStatus())
                    .educationName(educationName)
                    .build();

        member.setEducation(education); // 최종만 저장
        educationRepository.save(education);
        return educationName;
    }

    private School saveNewSchool(SchoolSaveRequestDto requestDto) throws CustomException {
        log.info("{} {} : 새로운 학교 정보 저장 중...", requestDto.name(), requestDto.major());
        return schoolRepository.saveIfNotExist(requestDto.name(), requestDto.major());
    }

    private String createEducation(SchoolSaveRequestDto requestDto) throws CustomException {
        return String.format("%s %s %s",
                requestDto.name(),
                requestDto.major(),
                requestDto.schoolStatus().getDisplayName());
    }
}
