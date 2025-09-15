package com.zluolan.zaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.zluolan.zaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 文件操作工具类
 * 提供基本的文件读写功能，用于AI Agent执行文件操作任务
 */
@Component
public class FileOperationTool {

    /**
     * 文件存储目录
     * 基于系统用户目录下的tmp文件夹构建文件存储路径
     */
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    /**
     * 读取指定文件的内容
     *
     * @param fileName 要读取的文件名
     * @return 文件内容或错误信息
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return "[TOOL_EXECUTION_RESULT][FILE_READ_SUCCESS] File content successfully read from: " + filePath + "\n[CONTENT]:\n" + FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "[TOOL_EXECUTION_RESULT][FILE_READ_ERROR] Error reading file: " + e.getMessage();
        }
    }

    /**
     * 将内容写入指定文件
     *
     * @param fileName 要写入的文件名
     * @param content  要写入的文件内容
     * @return 操作结果信息
     */
    @Tool(description = "Write content to a file")
    public String writeFile(
            @ToolParam(description = "Name of the file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "[TOOL_EXECUTION_RESULT][FILE_WRITE_SUCCESS] File successfully written to: " + filePath + " (Content length: " + content.length() + " characters)";
        } catch (Exception e) {
            return "[TOOL_EXECUTION_RESULT][FILE_WRITE_ERROR] Error writing to file: " + e.getMessage();
        }
    }
}