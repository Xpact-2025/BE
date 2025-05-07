package com.itstime.xpact.domain.member.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrieUtil {

    private final Trie trie;

    // Trie에 키워드 저장
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    // Trie 키워드 조회
    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie
                .prefixMap(keyword)
                .keySet()
                .stream()
                .collect(Collectors.toList());
    }

    // 특정 DB에 있는 것들 전부 Trie에 넣기
    public void loadDatasIntoTrie(List<String> dataList) {

        for (String data : dataList) {
            addAutocompleteKeyword(data);
        }
    }

    // Trie 목록 비우기
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
}
