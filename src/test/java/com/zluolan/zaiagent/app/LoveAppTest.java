package com.zluolan.zaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.document.DocumentWriter;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员AAAAA";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（BBBBBB）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }


    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithCloudRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithCloudRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithPgRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithPgRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看知乎的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("百度下载一张适合做手机壁纸的星空图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python 脚本，输出'Hellowo World'");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doIndividualToolTest() {
        String chatId = UUID.randomUUID().toString();
        
        System.out.println("=== 测试网页搜索 ===");
        String result1 = loveApp.doChatWithTools("请使用搜索引擎工具搜索上海适合情侣的小众约会地点", chatId);
        System.out.println("搜索结果: " + result1);

        
        System.out.println("\n=== 测试网页抓取 ===");
        String result2 = loveApp.doChatWithTools("请使用网页抓取工具访问百度首页https://www.baidu.com，获取页面标题", chatId);
        System.out.println("抓取结果: " + result2);// 先不进行断言，查看实际输出
        
        System.out.println("\n=== 测试资源下载 ===");
        String result3 = loveApp.doChatWithTools("请使用资源下载工具从https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png下载百度logo图片，保存为baidu_logo.png", chatId);
        System.out.println("下载结果: " + result3);

        
        System.out.println("\n=== 测试终端命令 ===");
        String result4 = loveApp.doChatWithTools("请使用终端执行工具执行 echo 'Hello World' 命令", chatId);
        System.out.println("命令执行结果: " + result4);

        
        System.out.println("\n=== 测试文件写入 ===");
        String result5 = loveApp.doChatWithTools("请使用文件操作工具创建一个名为test_love_note.txt的文件，内容为'这是一份恋爱笔记'", chatId);
        System.out.println("文件写入结果: " + result5);

        
        System.out.println("\n=== 测试PDF生成 ===");
        String result6 = loveApp.doChatWithTools("请使用PDF生成工具生成一个名为test_love_plan.pdf的PDF文件，内容为'七夕约会计划：1. 共进晚餐 2. 观看夜景 3. 互赠礼物'", chatId);
        System.out.println("PDF生成结果: " + result6);

    }
}