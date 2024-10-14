package sk.uteg.springdatatest.api;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sk.uteg.springdatatest.api.model.CampaignSummary;

@SpringBootTest
class CampaignControllerTest {

    @Autowired
    private CampaignController campaignController;


    @Test
    void testGetSummarySuccess() throws Exception {
        UUID campaignId = UUID.fromString("a9cf9e86-e7a5-4934-bbed-8aa03a086aed");
        ResponseEntity<CampaignSummary> response = campaignController.getSummary(campaignId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalFeedbacks()).isEqualTo(3);
        assertThat(response.getBody().getQuestionSummaries()).hasSize(2);
    }

    @Test
    void testGetSummaryCampaignNotFound() {
        UUID nonExistentCampaignId = UUID.randomUUID();

        ResponseEntity<CampaignSummary> response = campaignController.getSummary(nonExistentCampaignId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetSummaryThrowsException() {
        UUID invalidCampaignId = null;

        try {
            campaignController.getSummary(invalidCampaignId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).contains("Invalid UUID");
        }
    }
}