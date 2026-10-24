package com.buddy.opportunity.service;

import com.buddy.opportunity.dto.OpportunityEligibilityResponse;
import com.buddy.opportunity.dto.OpportunityRecommendationFeedResponse;
import com.buddy.opportunity.dto.OpportunityRecommendationResponse;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.dto.OpportunitySkillMatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OpportunityRecommendationService {

    private static final BigDecimal STRONG_MATCH_THRESHOLD =
            BigDecimal.valueOf(70);

    private final OpportunityService opportunityService;
    private final OpportunityEligibilityService eligibilityService;
    private final OpportunitySkillMatchService skillMatchService;

    public OpportunityRecommendationService(
            OpportunityService opportunityService,
            OpportunityEligibilityService eligibilityService,
            OpportunitySkillMatchService skillMatchService
    ) {
        this.opportunityService = opportunityService;
        this.eligibilityService = eligibilityService;
        this.skillMatchService = skillMatchService;
    }

    @Transactional(readOnly = true)
    public OpportunityRecommendationFeedResponse getRecommendations(
            String email
    ) {
        List<OpportunityRecommendationResponse> recommendations =
                new ArrayList<>();

        List<OpportunityRecommendationResponse> alternatives =
                new ArrayList<>();

        for (OpportunityResponse opportunity :
                opportunityService.getActiveOpportunities()) {

            OpportunityRecommendationResponse result =
                    buildRecommendation(email, opportunity);

            if ("RECOMMENDED".equals(result.recommendationStatus())) {
                recommendations.add(result);
            } else {
                alternatives.add(result);
            }
        }

        Comparator<OpportunityRecommendationResponse> ranking =
                Comparator.comparing(
                        OpportunityRecommendationResponse
                                ::skillMatchPercentage,
                        Comparator.reverseOrder()
                ).thenComparing(
                        result -> result.opportunity().title(),
                        String.CASE_INSENSITIVE_ORDER
                );

        recommendations.sort(ranking);

        alternatives.sort(
                Comparator.comparing(
                        OpportunityRecommendationResponse::eligible,
                        Comparator.reverseOrder()
                ).thenComparing(ranking)
        );

        return new OpportunityRecommendationFeedResponse(
                List.copyOf(recommendations),
                List.copyOf(alternatives)
        );
    }

    private OpportunityRecommendationResponse buildRecommendation(
            String email,
            OpportunityResponse opportunity
    ) {
        OpportunityEligibilityResponse eligibility =
                eligibilityService.checkEligibility(
                        email,
                        opportunity.id()
                );

        OpportunitySkillMatchResponse skillMatch =
                skillMatchService.matchSkills(
                        email,
                        opportunity.id()
                );

        boolean hasRequiredSkills =
                skillMatch.totalRequiredSkills() > 0;

        String status = determineStatus(
                eligibility.eligible(),
                skillMatch.matchPercentage(),
                hasRequiredSkills
        );

        List<String> reasons = buildReasons(
                eligibility,
                skillMatch,
                hasRequiredSkills
        );

        // The score represents skill-match percentage only.
        // Eligibility is a separate hard requirement for recommendations.
        BigDecimal score = skillMatch.matchPercentage()
                .setScale(2, RoundingMode.HALF_UP);

        return new OpportunityRecommendationResponse(
                opportunity,
                eligibility.eligible(),
                eligibility.reasons(),
                skillMatch.totalRequiredSkills(),
                skillMatch.matchedSkillCount(),
                skillMatch.matchedSkills(),
                skillMatch.missingSkills(),
                skillMatch.matchPercentage(),
                score,
                status,
                List.copyOf(reasons)
        );
    }

    private String determineStatus(
            boolean eligible,
            BigDecimal skillMatchPercentage,
            boolean hasRequiredSkills
    ) {
        if (!hasRequiredSkills) {
            return "INSUFFICIENT_SKILL_DATA";
        }

        if (!eligible) {
            if (skillMatchPercentage.compareTo(
                    STRONG_MATCH_THRESHOLD) >= 0) {
                return "SKILL_MATCH_BUT_INELIGIBLE";
            }

            return "NOT_CURRENTLY_SUITABLE";
        }

        if (skillMatchPercentage.compareTo(
                STRONG_MATCH_THRESHOLD) >= 0) {
            return "RECOMMENDED";
        }

        return "ELIGIBLE_NEEDS_SKILLS";
    }

    private List<String> buildReasons(
            OpportunityEligibilityResponse eligibility,
            OpportunitySkillMatchResponse skillMatch,
            boolean hasRequiredSkills
    ) {
        List<String> reasons = new ArrayList<>();

        if (!eligibility.eligible()) {
            reasons.add("Not recommended because eligibility requirements are not met.");

            reasons.addAll(eligibility.reasons());
        } else {
            reasons.add("Eligibility requirements are satisfied.");
        }

        if (!hasRequiredSkills) {
            reasons.add(
                    "No required skills are configured; skill-based ranking is unavailable."
            );

            return reasons;
        }

        reasons.add(
                "Completed " + skillMatch.matchedSkillCount()
                        + " of " + skillMatch.totalRequiredSkills()
                        + " required skills."
        );

        reasons.add(
                "Skill-match percentage: "
                        + skillMatch.matchPercentage() + "%."
        );

        if (!skillMatch.missingSkills().isEmpty()) {
            reasons.add(
                    "Skills to develop: "
                            + String.join(", ", skillMatch.missingSkills())
                            + "."
            );
        }

        if (eligibility.eligible()
                && skillMatch.matchPercentage().compareTo(
                        STRONG_MATCH_THRESHOLD) >= 0) {
            reasons.add(
                    "Ranked as recommended because eligibility is satisfied and skill match is at least 70%."
            );
        } else if (eligibility.eligible()) {
            reasons.add(
                    "Eligible, but completing more required skills may improve the match."
            );
        }

        return reasons;
    }
}
