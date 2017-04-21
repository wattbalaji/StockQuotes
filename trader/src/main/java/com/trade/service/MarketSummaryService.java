package com.trade.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.trade.domain.MarketSummary;
import com.trade.domain.Quote;
import com.trade.exception.SymbolNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

@Service

public class MarketSummaryService {
	private static final Logger logger = LoggerFactory.getLogger(MarketSummaryService.class);

	@Value("${pivotal.summary.quotes:3}")
	private Integer numberOfQuotes;
	
	//10 minutes in milliseconds
	@Value("${pivotal.summary.refresh:600000}")
	private final static String refresh_period = "600000";
	
	@Value("${pivotal.summary.symbols.it:EMC,IBM,VMW}")
	private String symbolsIT;
    @Value("${pivotal.summary.symbols.fs:JPM,C,MS}")
	private String symbolsFS;
    
    @Autowired
	private MarketService marketService;
    
	private MarketSummary summary = new MarketSummary();
	
	public MarketSummary getMarketSummary() {
		logger.debug("Retrieving Market Summary: " + summary);
		
		return summary;
	}
	

	
	@Scheduled(fixedRateString = refresh_period)
	protected void retrieveMarketSummary() throws SymbolNotFoundException {
		logger.debug("Scheduled retrieval of Market Summary");
		/*
		 * Sleuth currently doesn't work with parallelStream.
		 * TODO: re-implement parallel calls.
		 */
		//List<Quote> quotesIT = pickRandomThree(Arrays.asList(symbolsIT.split(","))).parallelStream().map(symbol -> getQuote(symbol)).collect(Collectors.toList());
		//List<Quote> quotesFS = pickRandomThree(Arrays.asList(symbolsFS.split(","))).parallelStream().map(symbol -> getQuote(symbol)).collect(Collectors.toList());
		
		//List<Quote> quotesFS = pickRandomThree(Arrays.asList(symbolsFS.split(","))).stream().map(symbol -> marketService.getQuote(symbol)).collect(Collectors.toList());
		summary.setTopGainers(getTopThree(symbolsIT));
		summary.setTopLosers(getTopThree(symbolsFS));
	}

	/**
	 * Retrieve the list of top winners/losers.
	 * Currently retrieving list of 3 random.
	 * @throws SymbolNotFoundException 
	 */
	private List<Quote> getTopThree(String symbols) throws SymbolNotFoundException {
		StringBuilder builder = new StringBuilder();
		for(Iterator<String> i = pickRandomThree(Arrays.asList(symbols.split(","))).iterator(); i.hasNext();) {
			builder.append(i.next());
			if (i.hasNext()) {
				builder.append(",");
			}
		}
		return marketService.getQuotes(builder.toString());
	}
	
	private List<String> pickRandomThree(List<String> symbols) {
		Collections.shuffle(symbols);
        List<String> list = symbols.subList(0, numberOfQuotes);
	    return list;
	}
}
