package com.itstime.xpact.global.openai;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class OpenAiRequestBuilder {

    // Score 산정
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

    // Feedback with Strength
    public String buildStrengthPrompt(String experiences, String strength) {
        StringBuilder builder = new StringBuilder();
        builder.append(strength).append("은 나의 경험 중 강점인 역량이다.\n");
        builder.append("첫번째 문단은 내 경험들을 토대로 50자 내외로 해당 역량이 강점인 이유 대하여 서술해줘.\n");
        builder.append("두번째 문단은 내 경험들을 토대로 50자 내외로 연결할 수 있는 커리어에 대하여 추천해줘.\n");
        builder.append("나의 경험들은 다음과 같다.\n");
        builder.append(experiences);

        return builder.toString();
    }

    // Feedback with Weakness
    public String buildWeaknessPrompt(String experiences, String weakness) {
        StringBuilder builder = new StringBuilder();
        builder.append(weakness).append("은 나의 경험 중 부족한 역량이다.\n");
        builder.append("첫번째 문단은 내 경험들을 토대로 50자 내외로 해당 역량이 약점인 이유 대하여 분석해줘.\n");
        builder.append("두번째 문단은 내 경험들을 토대로 50자 내외로 보완할 수 있는 추천 활동들을 제시해줘.\n");
        builder.append("나의 경험들은 다음과 같다.\n");
        builder.append(experiences);

        return builder.toString();
    }
}
