package sk.uteg.springdatatest.service;

import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.OptionSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
public class CampaingServiceTest {

    @Autowired
    private CampaignService campaingService;

    @Test
    void testGetSummaryWithExistingCampaign() {
        UUID campaignId = UUID.fromString("a9cf9e86-e7a5-4934-bbed-8aa03a086aed");

        CampaignSummary summary = campaingService.getSummary(campaignId);

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getTotalFeedbacks()).isEqualTo(3);
        assertThat(summary.getQuestionSummaries()).hasSize(2);

        // Check rating question summary
        QuestionSummary ratingSummary = summary.getQuestionSummaries().get(0);
        assertThat(ratingSummary.getName()).isEqualTo("Rating question");
        assertThat(ratingSummary.getRatingAverage()).isEqualTo("4.00");

        // Check choice question summary
        QuestionSummary choiceSummary = summary.getQuestionSummaries().get(1);
        assertThat(choiceSummary.getName()).isEqualTo("Choice question");

        // Verify option occurrences
        List<OptionSummary> optionSummaries = choiceSummary.getOptionSummaries();
        assertThat(optionSummaries).hasSize(3);

        OptionSummary option1 = optionSummaries.stream()
                .filter(opt -> opt.getText().equals("Option 1"))
                .findFirst().orElse(null);
        assertThat(option1).isNotNull();
        assertThat(option1.getOccurrences()).isEqualTo(1);

        OptionSummary option2 = optionSummaries.stream()
                .filter(opt -> opt.getText().equals("Option 2"))
                .findFirst().orElse(null);
        assertThat(option2).isNotNull();
        assertThat(option2.getOccurrences()).isEqualTo(3);

        OptionSummary option4 = optionSummaries.stream()
                .filter(opt -> opt.getText().equals("Option 4"))
                .findFirst().orElse(null);
        assertThat(option4).isNotNull();
        assertThat(option4.getOccurrences()).isEqualTo(2);
    }

    @Test
    void testGetSummaryWithNonExistingCampaign() {
        UUID nonExistentCampaignId = UUID.randomUUID();
        CampaignSummary summary = campaingService.getSummary(nonExistentCampaignId);
        assertThat(summary).isNull();
    }
}