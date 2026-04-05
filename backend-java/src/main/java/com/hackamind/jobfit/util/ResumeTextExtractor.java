package com.hackamind.jobfit.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResumeTextExtractor {
    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (filename.endsWith(".pdf")) {
            return extractPdf(file.getInputStream());
        }
        if (filename.endsWith(".docx")) {
            return extractDocx(file.getInputStream());
        }
        throw new IllegalArgumentException("Only PDF or DOCX files are allowed");
    }

    private String extractPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}
