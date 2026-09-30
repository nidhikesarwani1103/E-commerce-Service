package dev.nidhi.productservice.specification;

import dev.nidhi.productservice.models.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasTitleContaining(String search){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + search.toLowerCase() + "%"
                );
    }

    public static Specification<Product> hasCategoryId(Long categoryId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Product> hasMinPrice(Double minPrice){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Product> hasMaxPrice(Double maxPrice){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }

    public static Specification<Product> isNotDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Product> build(String search,
                                               Long categoryId,
                                               Double minPrice,
                                               Double maxPrice){

        Specification<Product> specification =
                ProductSpecification.isNotDeleted();

        if(search!=null && !search.isBlank()){
            Specification<Product> searchSpecification =
                    ProductSpecification.hasTitleContaining(search);

            specification = specification.and(searchSpecification);
        }

        if(categoryId!=null){
            Specification<Product> categorySpecification =
                    ProductSpecification.hasCategoryId(categoryId);

            specification = specification.and(categorySpecification);
        }

        if(minPrice!=null){
            Specification<Product> minPriceSpecification =
                    ProductSpecification.hasMinPrice(minPrice);

            specification = specification.and(minPriceSpecification);
        }

        if(maxPrice!=null){
            Specification<Product> maxPriceSpecification =
                    ProductSpecification.hasMaxPrice(maxPrice);

            specification = specification.and(maxPriceSpecification);
        }
      return specification;
    }

}
