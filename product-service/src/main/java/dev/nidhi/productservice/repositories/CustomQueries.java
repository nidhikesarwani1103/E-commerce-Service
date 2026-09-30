package dev.nidhi.productservice.repositories;

public interface CustomQueries {
     String PRODUCT_CATEGORY_RIGHT_JOIN =
            "SELECT * from product p RIGHT JOIN category c ON p.category_id = c.id";
}
