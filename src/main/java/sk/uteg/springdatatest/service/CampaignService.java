package sk.uteg.springdatatest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.OptionSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.db.model.Answer;
import sk.uteg.springdatatest.db.model.Campaign;
import sk.uteg.springdatatest.db.model.Question;
import sk.uteg.springdatatest.db.model.QuestionType;
import sk.uteg.springdatatest.db.repository.AnswerRepository;
import sk.uteg.springdatatest.db.repository.CampaignRepository;
import sk.uteg.springdatatest.db.repository.FeedbackRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private FeedbackRepository feedbackRepository;
    private CampaignRepository campaignRepository;
    private AnswerRepository answerRepository;

    @Autowired
    public CampaignService(FeedbackRepository feedbackRepository, CampaignRepository campaignRepository, AnswerRepository answerRepository) {
        this.feedbackRepository = feedbackRepository;
        this.campaignRepository = campaignRepository;
        this.answerRepository = answerRepository;
    }

    public CampaignSummary getSummary(UUID uuid) {
        Optional<Campaign> campaign = campaignRepository.findById(uuid);
        if(campaign.isEmpty()) {
            return null;
        } else {
            Campaign foundCampaign = campaign.get();
            CampaignSummary campaignSummary = new CampaignSummary();
            campaignSummary.setTotalFeedbacks(feedbackRepository.getTotalOfFeedback());
            campaignSummary.setQuestionSummaries(convertQuestionToQuestionSummary(foundCampaign.getQuestions()));
            return campaignSummary;
        }
    }


    private List<QuestionSummary> convertQuestionToQuestionSummary(List<Question> questions) {
        return questions.stream()
                .map((question) -> {
                    QuestionSummary summary = new QuestionSummary();
                    summary.setType(question.getType());
                    summary.setName(question.getText());
                    List<Answer> answers = answerRepository.findByQuestionId(question.getId());
                    if(question.getType() == QuestionType.RATING) {
                        BigDecimal ratingAverage = calculateRatingAverage(answers);
                        summary.setRatingAverage(ratingAverage);
                        summary.setOptionSummaries(new ArrayList<>());
                    } else {
                        summary.setRatingAverage(BigDecimal.ZERO);
                        summary.setOptionSummaries(getOptionSummaries(answers));
                    }
                    return summary;
                }).collect(Collectors.toList());
    }

    private List<OptionSummary> getOptionSummaries(List<Answer> answers) {
        Map<String, Integer> map = new HashMap<>();

        answers.stream()
                .flatMap(answer -> answer.getSelectedOptions().stream())
                .forEach(option -> map.merge(option.getText(), 1, Integer::sum));

        return map.entrySet().stream()
                .map(entry -> {
                    OptionSummary optionSummary = new OptionSummary();
                    optionSummary.setText(entry.getKey());
                    optionSummary.setOccurrences(entry.getValue());
                    return optionSummary;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateRatingAverage(List<Answer> answers) {
        if (answers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = answers
                .stream()
                .map(Answer::getRatingValue)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(answers.size()), 2, BigDecimal.ROUND_HALF_UP);
    }
}
