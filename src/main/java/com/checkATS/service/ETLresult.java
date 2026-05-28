package com.checkATS.service;
import java.util.List;

public record ETLresult (
    int alignmentScore,
    List<String> matchedSkills,
    List<String> missingKeywords,
    List<String> generalRecommendations,
    String optimizedResults
){}
