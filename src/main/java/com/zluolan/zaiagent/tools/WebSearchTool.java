package com.zluolan.zaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;

public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);

            // 解析返回结果
            JSONObject jsonObject = JSONUtil.parseObj(response);

            // 检查是否存在错误信息
            if (jsonObject.containsKey("error")) {
                return "Search API error: " + jsonObject.getStr("error");
            }

            // 检查搜索结果是否为空
            if (!jsonObject.containsKey("organic_results")) {
                return "No search results found. Full response: " + response;
            }

            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            if (organicResults == null || organicResults.isEmpty()) {
                return "No search results found.";
            }

            // 取出返回结果的前 5 条
            int resultCount = Math.min(5, organicResults.size());
            JSONArray topResults = new JSONArray();
            for (int i = 0; i < resultCount; i++) {
                topResults.add(organicResults.get(i));
            }

            // 格式化结果为易读的字符串
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < topResults.size(); i++) {
                if (i > 0) result.append("\n---\n");
                JSONObject item = topResults.getJSONObject(i);
                result.append("Title: ").append(item.getStr("title", "N/A")).append("\n");
                result.append("Link: ").append(item.getStr("link", "N/A")).append("\n");
                result.append("Snippet: ").append(item.getStr("snippet", "N/A"));
            }

            return result.toString();
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}
