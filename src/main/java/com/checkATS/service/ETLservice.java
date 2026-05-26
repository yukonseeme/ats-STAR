package com.checkATS.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ETLservice {

    private ChatModel chatModel;

    public ETLservice(ChatModel chatModel){
        this.chatModel = chatModel;
    }
    /**
     * Recommended for STAR analysis: Uses larger chunks to keep context intact.
     */
    public List<Document> processResumeForAnalysis(Resource pdfResource) {
        List<Document> rawPages = extractPages(pdfResource);

        TokenTextSplitter splitter = new TokenTextSplitter(
                1200, // defaultChunkSize
                400,  // minChunkSizeChars
                5,    // minChunkLengthToEmbed
                10000,// maxNumChunks
                true  // keepSeparator
        );

        return splitter.apply(rawPages);
    }

    /**
     * Standard ingestion: Uses default Spring AI settings.
     */
    public List<Document> processResumeDefault(Resource pdfResource) {
        List<Document> rawPages = extractPages(pdfResource);
        return new TokenTextSplitter().apply(rawPages);
    }

    public String compareResumeToJobDescription(ClassPathResource resumeFile, ClassPathResource jobDescription) throws Exception {
        List<Document> resumeChunks = processResumeForAnalysis(resumeFile);

        String resumeText = resumeChunks.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        String jobDescriptionText = Files.readString(jobDescription.getFile().toPath());

        // ai prompt
        String prompt = """
            You are an expert ATS (Applicant Tracking System) optimizer. 
            Compare the provided Resume text with the Job Description.
            
            Identify:
            1. Key skill matches (Hard and Soft skills).
            2. Missing critical keywords or skills.
            3. An alignment score from 0%% to 100%%.
            4. Actionable recommendations to tailor the resume to fit this specific role better.
            
            ---
            JOB DESCRIPTION:
            %s
            
            ---
            RESUME:
            %s
            """.formatted(jobDescriptionText, resumeText);

        return chatModel.call(prompt);
    }

    // Private helper to handle the "Extract" part of ETL
    private List<Document> extractPages(Resource pdfResource) {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
        return reader.get();
    }
}