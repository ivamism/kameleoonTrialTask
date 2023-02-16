package by.ivam.kameleoonTrialTask.service;

import by.ivam.kameleoonTrialTask.api.request.QuoteCreateRequest;
import by.ivam.kameleoonTrialTask.api.request.QuoteRequest;
import by.ivam.kameleoonTrialTask.api.response.QuoteResponse;
import by.ivam.kameleoonTrialTask.api.response.UserResponse;
import by.ivam.kameleoonTrialTask.exceptions.MyNoSuchElementException;
import by.ivam.kameleoonTrialTask.model.Quote;
import by.ivam.kameleoonTrialTask.model.Score;
import by.ivam.kameleoonTrialTask.model.User;
import by.ivam.kameleoonTrialTask.repositories.QuoteRepository;
import by.ivam.kameleoonTrialTask.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class QuoteService implements QuoteServiceInterface {
    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScoreService scoreService;

    @Autowired
    UserService userService;

    @Override
    public QuoteResponse findById(long id) {
        Quote quote = quoteRepository.findById(id).orElseThrow(() -> new MyNoSuchElementException("The Quote not found"));
        return quoteResponseCreator(quote);
    }


    @Override
    public QuoteResponse getRandomQuote() {
        Quote quote;
        while (true) {
            Random rndIdGen = new Random();
            long randomId = rndIdGen.nextInt(quoteRepository.countQuotesQuantity() - 1);
            Optional<Quote> optionalQuote = quoteRepository.findById(randomId);
            if (optionalQuote.isPresent()) {
                quote = optionalQuote.get();
                break;
            }
        }
        return quoteResponseCreator(quote);
    }

    @Override
    public QuoteResponse create(QuoteCreateRequest quoteCreateRequest) {
        Quote quote = new Quote();
        quote.setContent(quoteCreateRequest.getContent());
        quote.setUser(userRepository.findById(quoteCreateRequest.getUserId()).get());
        quote.setCreateDate(LocalDateTime.now());
        quote.setUpdateDate(LocalDateTime.now());
        quote.setScore(scoreService.create());
        quoteRepository.save(quote);
        return quoteResponseCreator(quote);
    }

    @Override
    public QuoteResponse update(long id, QuoteRequest quoteRequest) {
        Quote quote = quoteRepository.findById(id).get();
        quote.setContent(quoteRequest.getContent());
        quote.setUpdateDate(LocalDateTime.now());
        quoteRepository.save(quote);
        return quoteResponseCreator(quote);
    }

    @Override
    public void delete(long id) {
        quoteRepository.deleteById(id);
    }

    @Override
    public List<QuoteResponse> find10top() {
        List<Quote> top10 = quoteRepository.findTop10AllByOrderByScore_ScoreDesc();
        List<QuoteResponse> topQuotesResponse = new ArrayList<>();
        for (Quote quote : top10) {
            QuoteResponse quoteResponse = quoteResponseCreator(quote);
            topQuotesResponse.add(quoteResponse);
        }
        return topQuotesResponse;
    }

    @Override
    public List<QuoteResponse> find10flop() {
        List<Quote> top10 = quoteRepository.findTop10AllByOrderByScore_ScoreAsc();
        List<QuoteResponse> topQuotesResponse = new ArrayList<>();
        for (Quote quote : top10) {
            QuoteResponse quoteResponse = quoteResponseCreator(quote);
            topQuotesResponse.add(quoteResponse);
        }
        return topQuotesResponse;
    }

    @Override
    public int countQuotesQuantity() {
        return quoteRepository.countQuotesQuantity();
    }

    @Override
    public UserResponse findQuoteOwner(long id) {
        Quote quote = quoteRepository.findById(id).orElseThrow(() -> new MyNoSuchElementException("The Quote not found"));
        User quoteOwner = quote.getUser();
        return userService.userResponseCreator(quoteOwner);
    }

    @Override
    public Score getQuoterScore(long id) {
        Quote quote = quoteRepository.findById(id).orElseThrow(() -> new MyNoSuchElementException("The Quote not found"));
        return quote.getScore();
    }

    private QuoteResponse quoteResponseCreator(Quote quote) {
        QuoteResponse quoteResponse = new QuoteResponse();
        quoteResponse.setId(quote.getId());
        quoteResponse.setContent(quote.getContent());
        quoteResponse.setCreateDate(quote.getCreateDate());
        quoteResponse.setUpdateDate(quote.getUpdateDate());
        quoteResponse.setUserName(quote.getUser().getName());
        quoteResponse.setScore(quote.getScore().getScore());
        return quoteResponse;
    }
}
