package sk.uteg.springdatatest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.service.CampaignService;

import java.util.UUID;

@RestController("campaign")
public class CampaignController {

    private CampaignService campaingService;

    @Autowired
    public CampaignController(CampaignService campaingService) {
        this.campaingService = campaingService;
    }

    @GetMapping("/summary/{uuid}")
    public ResponseEntity<CampaignSummary> getSummary(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(campaingService.getSummary(uuid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
