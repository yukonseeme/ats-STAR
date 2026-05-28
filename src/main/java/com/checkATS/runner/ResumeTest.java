package com.checkATS.runner;

import com.checkATS.service.ETLservice;
import com.checkATS.service.ETLresult;
import org.springframework.ai.document.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ResumeTest implements CommandLineRunner {

    private final ETLservice etlService;

    public ResumeTest(ETLservice etlService) {
        this.etlService = etlService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting Resume ETL Test ===");

        ClassPathResource resumeFile = new ClassPathResource("MANALANSANG - RESUME.pdf");
        ClassPathResource jobDescription = new ClassPathResource("Job_Description.txt");

        if (!resumeFile.exists() || !jobDescription.exists()) {
            System.err.println("Error: File 'MANALANSANG - RESUME.pdf' or Job Description not found in resources!");
            return;
        }

        List<Document> chunks = etlService.processResumeForAnalysis(resumeFile);

        System.out.println("ETL Successful!");
        System.out.println("Total Chunks Created: " + chunks.size());

        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("--- Chunk " + (i + 1) + " ---");
            System.out.println(chunks.get(i).getContent());
            System.out.println("---------------------------------------------------");
        }

        System.out.println("\n=== Triggering AI Optimization Evaluation ===");

        ETLresult analysisResult = etlService.compareResumeToJobDescription(resumeFile, jobDescription);


        if (analysisResult != null) {
            System.out.println("--- ATS Analysis Metrics ---");
            System.out.println("Match Alignment Score: " + analysisResult.alignmentScore() + "%");
            System.out.println("Matched Core Skills: " + analysisResult.matchedSkills());
            System.out.println("Identified Skill Gaps / Missing Keywords: " + analysisResult.missingKeywords());
            System.out.println("Actionable Strategy Feedback: " + analysisResult.generalRecommendations());

            System.out.println("\n--- AI Reconstructed & Optimized Resume Copy ---");
            System.out.println(analysisResult.optimizedResults());
        } else {
            System.err.println("Error: AI optimizer execution returned a null result payload.");
        }
    }
}