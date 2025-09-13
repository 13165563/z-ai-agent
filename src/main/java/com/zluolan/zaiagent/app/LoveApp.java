package com.zluolan.zaiagent.app;


import com.alibaba.cloud.ai.autoconfigure.memory.MysqlChatMemoryProperties;
import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import com.zluolan.zaiagent.advisor.MyLoggerAdvisor;
import com.zluolan.zaiagent.chatmemeory.FileBasedChatMemoryRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel ollamaChatModel/*, MysqlChatMemoryRepository mysqlChatMemoryRepository*/) {
        // 初始化基于内存的对话记忆
        InMemoryChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();

        // 创建基于文件的记忆存储
        ChatMemoryRepository fileRepository =
                new FileBasedChatMemoryRepository("./chat_memories");

        int MAX_MESSAGES = 10;
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
//                .chatMemoryRepository(chatMemoryRepository)
//                .chatMemoryRepository(fileRepository)
//                .chatMemoryRepository(mysqlChatMemoryRepository)
                .maxMessages(MAX_MESSAGES)
                .build();

        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                .build();
    }

    /**
     * 测试自定义Re2 和 Logger 顾问
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                )
                .advisors(/*new ReReadingAdvisor()*/
                        /*new MyLoggerAdvisor()*/
                )
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    /**
     * 结构化输出
     */
    record LoveReport(String title, List<String> suggestions) {
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                        .param(CONVERSATION_ID, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    /**
     * 使用本地知识库
     */
    @Resource
    private VectorStore loveAppVectorStore;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                        .param(CONVERSATION_ID, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答（本地内存）
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;

    }


    /**
     * 使用云知识库服务
     */
    @Resource
    private Advisor loveAppRagCloudAdvisor;
    public String doChatWithCloudRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                        .param(CONVERSATION_ID, 10))
                // 开启日志，便于观察效果
//                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（云知识库服务）
                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 使用自建Postgres知识库
     */
    @Resource
    private VectorStore pgVectorVectorStore;
    public String doChatWithPgRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                        .param(CONVERSATION_ID, 10))
                // 开启日志，便于观察效果
//                .advisors(new MyLoggerAdvisor())
                // 应用增强检索服务（基于pg向量存储）
                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
