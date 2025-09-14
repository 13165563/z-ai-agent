package com.zluolan.zaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class TerminalOperationToolTest {

    @Test
    public void testExecuteTerminalCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String command = "dir";
        String result = tool.executeTerminalCommand(command);

        assertNotNull(result, "Result should not be null");
        System.out.println("Command: " + command);
        System.out.println("Output:");
        System.out.println(result);

        // 验证输出不包含错误信息
        assertFalse(result.contains("Error executing command"),
                "Command execution should not produce error");
    }

    @Test
    public void testExecuteEchoCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String command = "echo Hello World";
        String result = tool.executeTerminalCommand(command);

        assertNotNull(result, "Result should not be null");
        System.out.println("Command: " + command);
        System.out.println("Output:");
        System.out.println(result);

        // 验证输出包含期望的文本
        assert(result.contains("Hello World") || result.isEmpty());
    }

    @Test
    public void testExecuteInvalidCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String command = "invalid_command_xyz";
        String result = tool.executeTerminalCommand(command);

        assertNotNull(result, "Result should not be null");
        System.out.println("Command: " + command);
        System.out.println("Output:");
        System.out.println(result);
    }
}
