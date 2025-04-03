package com.itstime.xpact.global.openai;

import com.itstime.xpact.domain.experience.entity.Experience;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    public Future<String> summarizeExperience(Experience experience) {
        String message = "다음 hibernate 쿼리 설명해줘 [Hibernate] select m1_0.member_id,m1_0.member_status,m1_0.birth_date,m1_0.created_time,m1_0.education,m1_0.email,m1_0.imgurl,m1_0.inactive_date,m1_0.modified_time,m1_0.name,m1_0.password,m1_0.recruit_id,m1_0.role,m1_0.type from member m1_0 where m1_0.member_id=?";
        Prompt prompt = new Prompt(message);
        ChatResponse response = openAiChatModel.call(prompt);
        String result = response.getResult().getOutput().getText();
        log.info("result : {}", result);
        return new AsyncResult<>(result);
    }
}
