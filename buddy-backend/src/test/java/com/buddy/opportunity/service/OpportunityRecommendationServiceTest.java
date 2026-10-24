package com.buddy.opportunity.service;

import com.buddy.opportunity.dto.OpportunityEligibilityResponse;
import com.buddy.opportunity.dto.OpportunityRecommendationFeedResponse;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.dto.OpportunitySkillMatchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpportunityRecommendationServiceTest {

    private OpportunityService opportunityService;
    private OpportunityEligibilityService eligibilityService;
    private OpportunitySkillMatchService skillMatchService;
    private OpportunityRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        opportunityService = mock(OpportunityService.class);
        eligibilityService = mock(OpportunityEligibilityService.class);
        skillMatchService = mock(OpportunitySkillMatchService.class);

        recommendationService = new OpportunityRecommendationService(
                opportunityService,
                eligibilityService,
                skillMatchService
        );
    }

    @Test
    void eligibleOpportunityWithExactlySeventyPercentMatchShouldBeRecommended() {
        OpportunityResponse opportunity = opportunity(1L, "Java Internship");

        when(opportunityService.getActiveOpportunities())
                .thenReturn(List.of(opportunity));

        when(eligibilityService.checkEligibility("student@buddy.com", 1L))
                .thenReturn(new OpportunityEligibilityResponse(
                        1L, true, List.of()
                ));

        when(skillMatchService.matchSkills("student@buddy.com", 1L))
                .thenReturn(skillMatch(1L, 70, 7, 10));

        OpportunityRecommendationFeedResponse feed =
                recommendationService.getRecommendations("student@buddy.com");

        assertEquals(1, feed.recommendations().size());
        assertTrue(feed.alternatives().isEmpty());

        assertEquals(
                "RECOMMENDED",
                feed.recommendations().getFirst().recommendationStatus()
        );

        assertEquals(
                new BigDecimal("70.00"),
                feed.recommendations().getFirst().recommendationScore()
        );
    }

    @Test
    void eligibleOpportunityBelowSeventyPercentShouldBeAnAlternative() {
        OpportunityResponse opportunity = opportunity(2L, "Spring Internship");

        when(opportunityService.getActiveOpportunities())
                .thenReturn(List.of(opportunity));

        when(eligibilityService.checkEligibility("student@buddy.com", 2L))
                .thenReturn(new OpportunityEligibilityResponse(
                        2L, true, List.of()
                ));

        when(skillMatchService.matchSkills("student@buddy.com", 2L))
                .thenReturn(skillMatch(2L, 60, 3, 5));

        OpportunityRecommendationFeedResponse feed =
                recommendationService.getRecommendations("student@buddy.com");

        assertTrue(feed.recommendations().isEmpty());
        assertEquals(1, feed.alternatives().size());

        assertEquals(
                "ELIGIBLE_NEEDS_SKILLS",
                feed.alternatives().getFirst().recommendationStatus()
        );
    }

    @Test
    void ineligibleOpportunityShouldNeverBeRecommendedEvenWithStrongSkillMatch() {
        OpportunityResponse opportunity = opportunity(3L, "Backend Internship");

        when(opportunityService.getActiveOpportunities())
                .thenReturn(List.of(opportunity));

        when(eligibilityService.checkEligibility("student@buddy.com", 3L))
                .thenReturn(new OpportunityEligibilityResponse(
                        3L, false, List.of("CGPA requirement not met.")
                ));

        when(skillMatchService.matchSkills("student@buddy.com", 3L))
                .thenReturn(skillMatch(3L, 90, 9, 10));

        OpportunityRecommendationFeedResponse feed =
                recommendationService.getRecommendations("student@buddy.com");

        assertTrue(feed.recommendations().isEmpty());
        assertEquals(1, feed.alternatives().size());

        assertFalse(feed.alternatives().getFirst().eligible());

        assertEquals(
                "SKILL_MATCH_BUT_INELIGIBLE",
                feed.alternatives().getFirst().recommendationStatus()
        );
    }

    @Test
    void opportunityWithoutRequiredSkillsShouldBeAnAlternative() {
        OpportunityResponse opportunity = opportunity(4L, "General Internship");

        when(opportunityService.getActiveOpportunities())
                .thenReturn(List.of(opportunity));

        when(eligibilityService.checkEligibility("student@buddy.com", 4L))
                .thenReturn(new OpportunityEligibilityResponse(
                        4L, true, List.of()
                ));

        when(skillMatchService.matchSkills("student@buddy.com", 4L))
                .thenReturn(new OpportunitySkillMatchResponse(
                        4L,
                        0,
                        0,
                        List.of(),
                        List.of(),
                        new BigDecimal("0.00")
                ));

        OpportunityRecommendationFeedResponse feed =
                recommendationService.getRecommendations("student@buddy.com");

        assertTrue(feed.recommendations().isEmpty());
        assertEquals(1, feed.alternatives().size());

        assertEquals(
                "INSUFFICIENT_SKILL_DATA",
                feed.alternatives().getFirst().recommendationStatus()
        );
    }

    private OpportunityResponse opportunity(Long id, String title) {
        return new OpportunityResponse(
                id,
                title,
                "Buddy Test Organization",
                "Test opportunity",
                "INTERNSHIP",
                "Remote",
                "REMOTE",
                null,
                null,
                "https://example.com/apply",
                null,
                true,
                null,
                null
        );
    }

    private OpportunitySkillMatchResponse skillMatch(
            Long opportunityId,
            int percentage,
            int matched,
            int total
    ) {
        List<String> matchedSkills = java.util.stream.IntStream
                .range(0, matched)
                .mapToObj(index -> "MatchedSkill" + index)
                .toList();

        List<String> missingSkills = java.util.stream.IntStream
                .range(matched, total)
                .mapToObj(index -> "MissingSkill" + index)
                .toList();

        return new OpportunitySkillMatchResponse(
                opportunityId,
                total,
                matched,
                matchedSkills,
                missingSkills,
                BigDecimal.valueOf(percentage).setScale(2)
        );
    }
}
