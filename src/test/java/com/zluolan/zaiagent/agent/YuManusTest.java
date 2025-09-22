package com.zluolan.zaiagent.agent;

import com.zluolan.zaiagent.agent.YuManus;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
class YuManusTest {

    @Resource
    private YuManus yuManus;

    @Test
    void run() throws IOException {
        String userPrompt = """
                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";

        // 调用方法
        String pdfPath = yuManus.run(userPrompt);

        // 断言非空
        Assertions.assertNotNull(pdfPath, "生成的 PDF 路径不能为空");

        // 检查返回的是否是 PDF 路径
        File pdfFile = new File(pdfPath);
        Assertions.assertTrue(pdfFile.exists(), "PDF 文件应当生成在路径: " + pdfPath);

        // 检查是否是 PDF 文件（前几个字节应该是 "%PDF"）
        try (FileInputStream fis = new FileInputStream(pdfFile)) {
            byte[] header = new byte[4];
            int read = fis.read(header);
            Assertions.assertEquals(4, read, "无法读取 PDF 文件头");
            String headerStr = new String(header);
            Assertions.assertEquals("%PDF", headerStr, "文件头不是 PDF 格式，实际为: " + headerStr);
        }
    }
}
