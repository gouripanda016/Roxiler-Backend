
package com.jspiders.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jspiders.service.ProductService;

import comjspiders.pojo.PriceRangeCount;
import comjspiders.pojo.Product;

@RestController
@RequestMapping("/era")
@CrossOrigin(origins = "*")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	private List<Product> products;

	@GetMapping("/transactions")
	public List<Product> getTransactions(@RequestParam(required = false, defaultValue = "") String search,
			@RequestParam(required = false, defaultValue = "03") int month,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int perPage) {
		
		 products=productService.getTransactions(search, month, page, perPage);
		return products;
	}

	 @GetMapping("/statistics")
	    public Map<String, Object> getStatistics(@RequestParam(required = false, defaultValue = "3") int month) {
	        
	        List<Product> statistic = productService.getTransactionsForStat(month);
	        Object[] statistics = productService.getStatistics(statistic);
	        return Map.of(
	            "totalSaleAmount", statistics[0],
	            "totalSoldItems", statistics[1],
	            "totalNotSoldItems", statistics[2]
	        );
	    }
	@GetMapping("/bar-chart")
	public List<PriceRangeCount> getBarChart(@RequestParam(required = false, defaultValue = "10") int month) {
		return productService.getPriceRangeCounts(month);
	}

	@GetMapping("/pie-chart")
	public List<Map<String, Object>> getPieChart(@RequestParam(required = false, defaultValue = "10") int month) {
		return productService.getCategoryCounts(month).stream()
				.map(categoryCount -> Map.of("category", categoryCount[0], "itemCount", categoryCount[1])).toList();
	}

	@GetMapping("/combined-response")
	public Map<String, Object> getCombinedResponse(@RequestParam(required = false, defaultValue = "") String search,
			@RequestParam(required = false, defaultValue = "10") int month,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int perPage) {
		List<Product> transactions = productService.getTransactions(search, month, page, perPage);
		Object[] statistics = productService.getStatistics(products);
		List<PriceRangeCount> barChart = productService.getPriceRangeCounts(month);
		List<Map<String, Object>> pieChart = productService.getCategoryCounts(month).stream()
				.map(categoryCount -> Map.of("category", categoryCount[0], "itemCount", categoryCount[1])).toList();

		return Map.of("transactions", transactions,
				"statistics", Map.of("totalSaleAmount", statistics[0],
				"totalSoldItems", statistics[1],
				"totalNotSoldItems", statistics[2]),
				"barChart", barChart, "pieChart",
				pieChart);
}
}
