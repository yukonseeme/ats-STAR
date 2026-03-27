package com.checkATS.model;

import java.util.List;

public record OptimizationReport(
        double matchScore,
        List<STARSuggestion> suggestions
) {}

record STARSuggestion(
        String originalBullet,
        String improvedBullet,
        String reasoning
) {}