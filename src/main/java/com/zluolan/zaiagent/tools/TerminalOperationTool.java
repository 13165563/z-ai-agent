package com.zluolan.zaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具类
 * 提供在系统终端中执行命令的功能，用于AI Agent执行系统级操作任务
 */
@Component
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        if (command == null || command.isEmpty()) {
            return "[TOOL_EXECUTION_RESULT][COMMAND_ERROR] Error: Command cannot be empty";
        }

        StringBuilder output = new StringBuilder();
        try {
            // 创建进程构建器
            ProcessBuilder builder = new ProcessBuilder();

            // 根据操作系统设置命令
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }

            builder.redirectErrorStream(true); // 合并错误流和输出流
            Process process = builder.start();

            // 读取命令执行的输出结果
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 等待命令执行完成并检查退出码
            int exitCode = process.waitFor();

            String result = "[TOOL_EXECUTION_RESULT][COMMAND_EXECUTION_RESULT]\n" +
                    "[COMMAND]: " + command + "\n" +
                    "[EXIT_CODE]: " + exitCode + "\n" +
                    "[OUTPUT]:\n" + (!output.isEmpty() ? output.toString() : "No output");

            // 如果是Python脚本相关错误，提供更具体的建议
            if (exitCode == 9009 || (output.toString().contains("不是内部或外部命令") ||
                    output.toString().contains("command not found"))) {
                result += "\n\n[SUGGESTION]:\n" +
                        "1. Check if the command is spelled correctly\n" +
                        "2. Confirm that Python is installed and in the system PATH\n" +
                        "3. If it's a script file, please provide the full path\n" +
                        "4. Ensure the script file has execution permissions";
            }

            return result;
        } catch (IOException e) {
            return "[TOOL_EXECUTION_RESULT][COMMAND_IO_ERROR] IO error occurred while executing command: " + e.getMessage() +
                    "\nPlease check if the command is correct or if system permissions are sufficient.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "[TOOL_EXECUTION_RESULT][COMMAND_INTERRUPTED] Command execution was interrupted: " + e.getMessage();
        } catch (Exception e) {
            return "[TOOL_EXECUTION_RESULT][COMMAND_EXCEPTION] Unexpected error occurred during command execution: " + e.getMessage();
        }
    }
}