package com.itstime.xpact.domain.member.service;

import com.itstime.xpact.domain.member.repository.SchoolCustomRepositoryImpl;
import com.itstime.xpact.domain.member.util.TrieUtil;
import com.itstime.xpact.global.auth.TokenProvider;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final TokenProvider tokenProvider;
    private final TrieUtil trieUtil;
    private final SchoolCustomRepositoryImpl schoolRepository;

    @Transactional(readOnly = true)
    public List<String> autocompleteName(
            String token, String term) throws CustomException {

        try {
            tokenProvider.validationToken(token);

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
    public List<String> searchMajor(String token, String schoolName) throws CustomException {

        return schoolRepository.findMajorBySchoolName(schoolName);
    }
}
