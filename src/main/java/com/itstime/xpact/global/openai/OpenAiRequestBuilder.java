package com.itstime.xpact.global.openai;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class OpenAiRequestBuilder {

    public String buildScorePrompt(String experiences, List<String> coreSkills) {
            StringBuilder builder = new StringBuilder();
            builder.append("다음은 나의 경험 요약본들을 모은 것이다.\n\n");
            builder.append(experiences).append("\n");
            builder.append("주어진 역량 키워드 각각에 대해 0.0 ~ 10.0의 점수를 부여해줘.\n");
            builder.append("경험을 종합적으로 고려해, 각 역량이 어느 정도 드러나는지 판단해 객관적으로 점수를 부여해줘.\n");

            for (int i = 0; i < coreSkills.size(); i++) {
                builder.append(String.format("{core_skill_%d}/", i+1));
            }
            return builder.toString();
    }

    public Map<String, String> buildScoreVariables(String experiences, List<String> coreSkills) {
        Map<String, String> variables = new HashMap<>();
        variables.put("experiences", experiences);
        for (int i = 0; i< coreSkills.size(); i++) {
            variables.put("core_skill_"+ (i+1), coreSkills.get(i));
        }
        return variables;
    }
}
