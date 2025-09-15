package com.zluolan.zaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class WebScrapingTool {

    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            // 添加请求头，模拟浏览器访问
            Document doc = Jsoup.connect(url)
                    .timeout(15000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .followRedirects(true)
                    .get();

            String title = doc.title();
            String content = doc.body().text();

            // 获取部分文本内容
            String excerpt = content.length() > 500 ? content.substring(0, 500) + "..." : content;

            return "[TOOL_EXECUTION_RESULT][WEB_SCRAPING_SUCCESS]\n" +
                    "[URL]: " + url + "\n" +
                    "[PAGE_TITLE]: " + title + "\n" +
                    "[CONTENT_SUMMARY]: " + excerpt;
        } catch (IOException e) {
            return "[TOOL_EXECUTION_RESULT][WEB_SCRAPING_ERROR] Web scraping failed: " + e.getMessage() + ". Please check if the URL is correct or the website is accessible.";
        } catch (Exception e) {
            return "[TOOL_EXECUTION_RESULT][WEB_SCRAPING_EXCEPTION] Unexpected error occurred during web scraping: " + e.getMessage();
        }
    }
}