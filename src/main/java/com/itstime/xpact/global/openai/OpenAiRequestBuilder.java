package com.itstime.xpact.global.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpenAiRequestBuilder {

    public static String createFunction(
            String modelName, String experiences, List<String> coreSkills, ObjectMapper mapper) throws CustomException {

        String message = String.format("다음은 사용자 경험들 요약을 모은 내용이다.\n" +
                "%s" +
                "\n해당 경험들에 대하여 5가지 역량을 기준으로 점수를 0.0 ~ 10.0으로 JSON으로 평가해줘. 각 5가지 역량은 다음과 같다. \n",
        experiences, coreSkills.toString());

        Map<String, Object> prompt = Map.of(
                "role", "user",
                "content", message
        );

        Map<String, Object> properties = new LinkedHashMap<>();
        for (String coreSkill : coreSkills) {
            properties.put(coreSkill, Map.of(
                    "type", "number",
                    "description", coreSkill + "의 점수"
            ));
        }

        Map<String, Object> parameters = Map.of(
                "type", "object",
                "properties", properties,
                "required", properties.keySet()
        );

        Map<String, Object> functionSchema = Map.of(
                "name", "score_experiences",
                "description", "사용자의 경험을 기반으로 5가지 역량에 대한 점수를 평가합니다.",
                "parameters", parameters
        );

        Map<String, Object> payload = Map.of(
                "model", modelName,
                "messages", List.of(prompt),
                "functions", List.of(functionSchema),
                "function_call", Map.of("name", "score_experiences")
        );

        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("지정 함수 생성 중 오류... {}", e.getMessage());
            throw CustomException.of(ErrorCode.FAILED_OPENAI_PARSING);
        }
    }
}
