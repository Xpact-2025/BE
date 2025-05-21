package com.itstime.xpact.global.openai;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class OpenAiRequestBuilder {

    public String buildScorePrompt(String experiences, List<String> coreSkills) {
            StringBuilder builder = new StringBuilder();
            builder.append("당신은 인사 전문가이며, 지원자의 직무 역량을 평가하는 \"AI evaluator for job competency\"이다.\n");
            builder.append("다음은 지원자의 경험 요약본들을 모은 것이다.\n\n");
            builder.append(experiences).append("\n");
            builder.append("주어진 역량 키워드 각각에 대해 지원자가 직무에서 보여준 역량을 객관적으로 평가해줘.");
            builder.append("평가지침은 다음과 같다.\n");
            builder.append("""
                    1. 각 역량 키워드에 대해 설명 없이 0.0 ~ 10.0 사이의 소수점 1자리 점수를 부여한다.
                    2. 점수는 해당 역량이 경험에서 얼마나 명확히 드러나는지를 기준으로 정량적으로 평가해라.
                    3. 모든 역량의 점수는 서로 중복되지 않도록 고유한 값을 가져야한다.
                    4. 경험이 명확히 부족한 경우에는 0.0 또는 그에 가까운 낮은 점수를 부여한다.
                    """);

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
