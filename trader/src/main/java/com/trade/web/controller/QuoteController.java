package com.trade.web.controller;



import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.servlet.ModelAndView;


import com.trade.exception.SymbolNotFoundException;
import com.trade.service.MarketSummaryService;


@Controller
public class QuoteController {
	private static final Logger logger = LoggerFactory
			.getLogger(QuoteController.class);
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(Model model) throws SymbolNotFoundException {
		
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		
		return "index";
	}
	
	@ExceptionHandler({ Exception.class })
	public ModelAndView error(HttpServletRequest req, Exception exception) {
		logger.debug("Handling error: " + exception);
		logger.warn("Exception:", exception);
		ModelAndView model = new ModelAndView();
		model.addObject("errorCode", exception.getMessage());
		model.addObject("errorMessage", exception);
		model.setViewName("error");
		return model;
	}
}
