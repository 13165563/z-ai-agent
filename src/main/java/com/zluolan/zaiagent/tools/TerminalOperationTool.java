package com.zluolan.zaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具类
 * 提供在系统终端中执行命令的功能，用于AI Agent执行系统级操作任务
 */
public class TerminalOperationTool {

    /**
     * 在终端中执行指定命令
     *
     * @param command 要执行的终端命令
     * @return 命令执行结果或错误信息
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();
        try {
            // 创建进程构建器，使用 Windows cmd 执行命令
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = builder.start();

            // 读取命令执行的输出结果
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"))) { // 使用 GBK 编码处理中文
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 等待命令执行完成并检查退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // 处理命令执行过程中的异常
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
