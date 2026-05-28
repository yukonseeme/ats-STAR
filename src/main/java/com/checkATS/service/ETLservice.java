package com.checkATS.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ETLservice {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ETLservice(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public ETLresult compareResumeToJobDescription(Resource resumeFile, Resource jobDescription) throws Exception {
        List<Document> resumeChunks = processResumeForAnalysis(resumeFile);
        String resumeText = resumeChunks.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        String jobDescriptionText = jobDescription.getContentAsString(StandardCharsets.UTF_8);

        String promptTemplate = """
            You are an expert ATS (Applicant Tracking System) optimizer.
            Compare the provided Resume text with the Job Description.
            
            Reconstruct the resume text inside 'optimizedResumeMarkdown' property by writing bullet points using action verbs that mirror the keywords found in the Job Description.
            
            ---
            JOB DESCRIPTION:
            {jobDescription}
            
            ---
            RESUME:
            {resume}
            
            ---
            CRITICAL REQUIREMENT: You must respond ONLY with a raw JSON object matching the keys below. Do not wrap it in markdown code blocks like ```json. Do not include any text before or after the JSON.
            
            Expected JSON Structure:
            {{
              "alignmentScore": 85,
              "matchedSkills": ["Skill1", "Skill2"],
              "missingKeywords": ["Keyword1", "Keyword2"],
              "generalRecommendations": ["Recommendation 1", "Recommendation 2"],
              "optimizedResults": "Full rewritten resume content in markdown formatting goes here"
            }}
            """;

        String rawJsonResponse = chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("jobDescription", jobDescriptionText)
                        .param("resume", resumeText))
                .call()
                .content();

        // formatter-safe extraction
        String jsonTag = "```" + "json";
        String closingTag = "```";

        if (rawJsonResponse.contains(jsonTag)) {
            rawJsonResponse = rawJsonResponse.substring(rawJsonResponse.indexOf(jsonTag) + 7, rawJsonResponse.lastIndexOf(closingTag));
        } else if (rawJsonResponse.contains(closingTag)) {
            rawJsonResponse = rawJsonResponse.substring(rawJsonResponse.indexOf(closingTag) + 3, rawJsonResponse.lastIndexOf(closingTag));
        }

        return objectMapper.readValue(rawJsonResponse.trim(), ETLresult.class);
    }

    public List<Document> processResumeForAnalysis(Resource pdfResource) {
        List<Document> rawPages = new PagePdfDocumentReader(pdfResource).get();
        TokenTextSplitter splitter = new TokenTextSplitter(1200, 400, 5, 10000, true);
        return splitter.apply(rawPages);
    }
}