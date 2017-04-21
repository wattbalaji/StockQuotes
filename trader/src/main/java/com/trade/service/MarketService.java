package com.trade.service;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;



import com.trade.domain.Quote;
import com.trade.exception.SymbolNotFoundException;



@Service

public class MarketService {
	private static final Logger logger = LoggerFactory.getLogger(MarketService.class);

	@Autowired
	private  QuoteService quoteService;
	
	
	/**
	 * Retrieve multiple quotes.
	 * 
	 * @param symbols comma separated list of symbols.
	 * @return
	 * @throws SymbolNotFoundException 
	 */

	public List<Quote> getQuotes(String symbols) throws SymbolNotFoundException {
		logger.debug("retrieving multiple quotes: " + symbols);
		
		List<Quote> quotes=	quoteService.getQuotes(symbols);
	
		logger.debug("Received quotes: {}",quotes);
		return quotes;
	}


	public List<Quote> getQuotes(String[] symbols) throws SymbolNotFoundException {
		logger.debug("Fetching multiple quotes array: {} ",Arrays.asList(symbols));
		
		return getQuotes(Arrays.asList(symbols));
	}

	public List<Quote> getQuotes(Collection<String> symbols) throws SymbolNotFoundException {
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

}
