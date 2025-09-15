package com.zluolan.zaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.zluolan.zaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        // 验证输入参数
        if (url == null || url.isEmpty()) {
            return "[TOOL_EXECUTION_RESULT][DOWNLOAD_ERROR] Error: URL cannot be empty";
        }

        if (fileName == null || fileName.isEmpty()) {
            return "[TOOL_EXECUTION_RESULT][DOWNLOAD_ERROR] Error: File name cannot be empty";
        }

        // 验证URL格式
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "[TOOL_EXECUTION_RESULT][DOWNLOAD_ERROR] Error: Invalid URL format. Please start with http:// or https://";
        }

        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(fileDir);

            // 检查文件是否已存在
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                return "[TOOL_EXECUTION_RESULT][DOWNLOAD_EXISTS] File already exists: " + filePath;
            }

            // 尝试下载资源
            long startTime = System.currentTimeMillis();
            HttpUtil.downloadFile(url, targetFile);
            long endTime = System.currentTimeMillis();

            if (targetFile.exists()) {
                long fileSize = targetFile.length();
                return "[TOOL_EXECUTION_RESULT][DOWNLOAD_SUCCESS] Resource successfully downloaded to: " + filePath +
                        " (File size: " + fileSize + " bytes, Time taken: " + (endTime - startTime) + "ms)";
            } else {
                return "[TOOL_EXECUTION_RESULT][DOWNLOAD_ERROR] Download completed but file not found. Please check if the download was successful.";
            }
        } catch (Exception e) {
            return "[TOOL_EXECUTION_RESULT][DOWNLOAD_ERROR] Failed to download resource: " + e.getMessage() + ". Please check if the URL is correct or the network is accessible.";
        }
    }
}