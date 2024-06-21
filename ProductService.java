
package com.jspiders.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jspiders.repository.ProductRepository;

import ch.qos.logback.classic.Logger;
import comjspiders.pojo.PriceRangeCount;
import comjspiders.pojo.Product;

@Service
public class ProductService {
	
	  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void initData() {
        String url = "https://s3.amazonaws.com/roxiler.com/product_transaction.json";
        Product[] products = restTemplate.getForObject(url, Product[].class);

        if (products != null) {
            for (Product product : products) {
                productRepository.save(product);
            }
        }
    }

    private Date convertStringToDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> getTransactions(String search, int month, int page, int perPage) {
        return productRepository.searchProducts(search, month, PageRequest.of(page - 1, perPage));
    }
 
    public List<Product> getTransactionsForStat(int month) {
        return productRepository.searchProducts(month);
    }
    public List<PriceRangeCount> getPriceRangeCounts(int month) {
        List<Product> products = productRepository.findProductsByMonth(month);
        long[] counts = new long[10];
        for (Product product : products) {
            double price = product.getPrice();
            if (price <= 100) counts[0]++;
            else if (price <= 200) counts[1]++;
            else if (price <= 300) counts[2]++;
            else if (price <= 400) counts[3]++;
            else if (price <= 500) counts[4]++;
            else if (price <= 600) counts[5]++;
            else if (price <= 700) counts[6]++;
            else if (price <= 800) counts[7]++;
            else if (price <= 900) counts[8]++;
            else counts[9]++;
        }

        return List.of(
            new PriceRangeCount("0 - 100", counts[0]),
            new PriceRangeCount("101 - 200", counts[1]),
            new PriceRangeCount("201 - 300", counts[2]),
            new PriceRangeCount("301 - 400", counts[3]),
            new PriceRangeCount("401 - 500", counts[4]),
            new PriceRangeCount("501 - 600", counts[5]),
            new PriceRangeCount("601 - 700", counts[6]),
            new PriceRangeCount("701 - 800", counts[7]),
            new PriceRangeCount("801 - 900", counts[8]),
            new PriceRangeCount("901-above", counts[9])
        );
    }

    public List<Object[]> getCategoryCounts(int month) {
        List<Product> products = productRepository.findProductsByMonth(month);
        return products.stream()
            .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()))
            .entrySet().stream()
            .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPieChart(int month) {
        return null;
    }

    public Object[] getStatistics(List<Product> products) {
        double totalSaleAmount = 0.0;
        long totalSoldItems = 0;
        long totalNotSoldItems = 0;

        for (Product product : products) {
            if (product.isSold()) {
                totalSaleAmount += product.getPrice();
                ++totalSoldItems;
            } else {
                ++totalNotSoldItems;
            }
        }

        logger.info("Total Sold Items: " + totalSoldItems + 
                    " Total Not Sold Items: " + totalNotSoldItems + 
                    " Total Sale Amount: " + totalSaleAmount);

        return new Object[]{totalSaleAmount, totalSoldItems, totalNotSoldItems};
    }
}
