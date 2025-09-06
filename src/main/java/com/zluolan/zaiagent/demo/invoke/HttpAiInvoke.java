package com.zluolan.zaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpException;
import cn.hutool.json.JSONObject;


public class HttpAiInvoke {

    public static void main(String[] args) {
        // 设置 API Key
        String apiKey = TestApiKey.API_KEY; // 使用测试API Key

        // 构造请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");

        JSONObject input = new JSONObject();
        JSONObject[] messages = new JSONObject[2];
        messages[0] = new JSONObject().set("role", "system").set("content", "You are a helpful assistant.");
        messages[1] = new JSONObject().set("role", "user").set("content", "你是谁？");
        input.set("messages", messages);
        requestBody.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        try {
            // 发送 POST 请求
            HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .execute();

            // 输出响应
            System.out.println("Response Status: " + response.getStatus());
            System.out.println("Response Body: " + response.body());
        } catch (HttpException e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}