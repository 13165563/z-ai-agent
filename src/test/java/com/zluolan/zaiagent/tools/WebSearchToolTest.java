package com.zluolan.zaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class WebSearchToolTest {

    @Value("${search-api.api-key:}")  // 添加默认值防止为空
    private String searchApiKey;

    @Test
    public void testSearchWeb() {
        WebSearchTool tool = new WebSearchTool(searchApiKey);
        String query = "程序员鱼皮编程导航 codefather.cn";
        String result = tool.searchWeb(query);

        assertNotNull(result, "Result should not be null");
        System.out.println("Search result:");
        System.out.println(result);

        // 验证结果不包含错误信息
        assertTrue(!result.contains("Error searching Baidu") ||
                        result.contains("No search results found"),
                "Search should not fail with exception");
    }

    @Test
    public void testSearchWithDebugInfo() {
        WebSearchTool tool = new WebSearchTool(searchApiKey);
        String query = "人工智能";
        String result = tool.searchWeb(query);

        System.out.println("Debug search result for '" + query + "':");
        System.out.println(result);
        System.out.println("Result length: " + result.length());
    }
}
