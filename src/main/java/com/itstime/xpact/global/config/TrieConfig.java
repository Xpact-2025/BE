package com.itstime.xpact.global.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrieConfig {

    // Trie Config
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<String>();
    }

}
