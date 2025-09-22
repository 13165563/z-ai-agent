package com.zluolan.zaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.zluolan.zaiagent.constant.FileConstant;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content and optional images")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content,
            @ToolParam(description = "List of image file paths to embed into the PDF") List<String> imagePaths) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            FileUtil.mkdir(fileDir);

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);

                // 写入正文
                document.add(new Paragraph(content));

                // 写入图片
                if (imagePaths != null) {
                    for (String path : imagePaths) {
                        try {
                            ImageData imageData = ImageDataFactory.create(path);
                            Image image = new Image(imageData).setAutoScale(true);
                            document.add(image);
                        } catch (Exception e) {
                            System.err.println("⚠️ 图片加载失败: " + path + " - " + e.getMessage());
                        }
                    }
                }
            }

            return "[TOOL_EXECUTION_RESULT][PDF_GENERATION_SUCCESS] PDF successfully generated to: " + filePath;
        } catch (IOException e) {
            return "[TOOL_EXECUTION_RESULT][PDF_GENERATION_ERROR] Error generating PDF: " + e.getMessage();
        }
    }
}
