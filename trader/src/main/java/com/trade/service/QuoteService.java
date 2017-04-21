package com.trade.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.trade.domain.Quote;
import com.trade.domain.QuoteMapper;
import com.trade.domain.YahooQuote;
import com.trade.domain.YahooQuoteResponses;
import com.trade.exception.SymbolNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class QuoteService {
	
	@Value("${pivotal.quotes.quotes_url}")
	protected String quote_url;
	@Value("${pivotal.quotes.companies_url}")
	protected String company_url;

	@Value("${pivotal.quotes.yahoo_rest_query}")
	protected String yahoo_url;// = "https://query.yahooapis.com/v1/public/yql?q=select * from yahoo.finance.quotes where symbol in ('{symbol}')&format={fmt}&env={env}";
	
	//https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22ibm%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=

	@Value("${pivotal.quotes.yahoo_env}")
	protected String ENV = "http://datatables.org/alltables.env";

	public static final String FMT = "json";

	private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);


	private RestTemplate restTemplate;

    public QuoteService() {
        restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(messageConverter);
        restTemplate.setMessageConverters(messageConverters);
    }

    /**
     * Retrieve one or more quotes.
     *
     * @param symbols comma delimited list of symbols.
     * @return a list of quotes.
     */
 
    public List<Quote> getQuotes(String symbols) throws SymbolNotFoundException {
        logger.debug("retrieving quotes for: " + symbols);
        if ( symbols.isEmpty() ) return new ArrayList<>();

        YahooQuoteResponses responses = restTemplate.getForObject(yahoo_url, YahooQuoteResponses.class, symbols, FMT, ENV);
        logger.debug("Got responses: " + responses);
        List<YahooQuote> yahooQuotes = responses.getResults().getQuoteList().getQuote();
        Date createDate = responses.getResults().getCreated();
        
        

        List<Quote> quotes = yahooQuotes.stream()
                .map(yQuote -> QuoteMapper.INSTANCE.mapFromYahooQuote(yQuote, createDate))
                .collect(Collectors.toList());
        
        List<Quote> finalQuotes= new ArrayList<Quote>();

        for (Quote quote : quotes) {
            if ( quote.getName() != null ) {
            	finalQuotes.add(quote);
            }
        }
        return finalQuotes;
    }
    
    
	public List<Quote> getQuotes(Collection<String> symbols) throws SymbolNotFoundException  {
		logger.debug("Fetching multiple quotes array: {} ",symbols);
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> i = symbols.iterator(); i.hasNext();) {
			builder.append(i.next());
			if (i.hasNext()) {
				builder.append(",");
			}
		}
		return getQuotes(builder.toString());
	}



	@SuppressWarnings("unused")
	private List<Quote> getQuotesFallback(String symbols) {
		logger.debug("QuoteService.getQuoteFallback: circuit opened for symbols: " + symbols);
		throw new RuntimeException("Quote service unavailable.");
	}

}
