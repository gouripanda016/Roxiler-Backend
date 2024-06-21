
package com.jspiders.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import comjspiders.pojo.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{ '$expr': { '$eq': [ { '$month': '$dateOfSale' }, ?0 ] } }")
    List<Product> findByDateOfSaleMonth(int month);

    @Query("{ '$and': [ { '$or': [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'price': ?0 } ] }, { '$expr': { '$eq': [ { '$month': '$dateOfSale' }, ?1 ] } } ] }")
    List<Product> searchProducts(String search, int month, org.springframework.data.domain.Pageable pageable);

    
    @Query("{ '$expr': { '$eq': [ { '$month': '$dateOfSale' }, ?0 ] } }")
    List<Product> findProductsByMonth(int month);
    
    

    @Query("{ '$expr': { '$eq': [ { '$month': '$dateOfSale' }, ?0 ] } }")
    List<Product> searchProducts(int month);


}
