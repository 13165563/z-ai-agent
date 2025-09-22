package com.zluolan.zaiagent.agent;

import com.zluolan.zaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class YuManus extends ToolCallAgent {  
  
    public YuManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);  
        this.setName("yuManus");  
        String SYSTEM_PROMPT = """  
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                When you have fully completed the user's request, you MUST call the `doTerminate` tool to end the interaction.
                Do not continue processing or repeat the same response if the task is complete.
                You must always call tools via structured ToolCalls.\s
                Do not simulate tool execution in plain text.
                If a user request requires external actions (file creation, search, PDF generation),\s
                use the corresponding tool instead of only responding with text.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);  
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                IMPORTANT: If you have finished all the tasks or if you cannot proceed further, you MUST call the `doTerminate` tool to end the interaction.
                Do not repeat the same process or response when the task is complete.
                """;  
        this.setNextStepPrompt(NEXT_STEP_PROMPT);  
        this.setMaxSteps(20);  
        // 初始化客户端  
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();  
        this.setChatClient(chatClient);  
    }  
}