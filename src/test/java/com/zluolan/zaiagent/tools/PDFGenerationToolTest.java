package com.zluolan.zaiagent.tools;

import com.zluolan.zaiagent.tools.PDFGenerationTool;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "编程导航原创项目.pdf";
        String content = "编程导航原创项目 https://www.codefather.cn";

        String result = tool.generatePDF(fileName, content, null);

        assertNotNull(result);
        assertTrue(result.contains("PDF successfully generated"));

        // 断言文件是否真的生成了
        File pdfFile = new File("C:\\ProgramFiles\\AllCodeProjects\\z-ai-agent/tmp/pdf/" + fileName);
        assertTrue(pdfFile.exists(), "PDF 文件应当生成在路径: " + pdfFile.getAbsolutePath());
    }
}
