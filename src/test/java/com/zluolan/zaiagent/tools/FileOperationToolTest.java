package com.zluolan.zaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileOperationTool 测试类
 * 测试文件读写工具的基本功能
 */

class FileOperationToolTest {

    /**
     * 测试文件写入功能
     */
    @Test
    void testWriteFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test.txt";
        String content = "Hello, this is a test file content.";

        String result = fileOperationTool.writeFile(fileName, content);
        assertTrue(result.contains("File written successfully"), 
            "文件写入应该成功并返回成功信息");
    }

    /**
     * 测试文件读取功能
     */
    @Test
    void testReadFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test-read.txt";
        String content = "Hello, this is a test file content for reading.";

        // 先写入文件
        fileOperationTool.writeFile(fileName, content);

        // 再读取文件
        String result = fileOperationTool.readFile(fileName);
        assertEquals(content, result, "读取的文件内容应该与写入的内容一致");
    }

    /**
     * 测试读取不存在的文件
     */
    @Test
    void testReadNonExistentFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "non-existent-file.txt";

        String result = fileOperationTool.readFile(fileName);
        assertTrue(result.contains("Error reading file:"), 
            "读取不存在的文件应该返回错误信息");
    }
}