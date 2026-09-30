package dev.nidhi.productservice.services;

import dev.nidhi.productservice.dtos.ProductDTO;
import dev.nidhi.productservice.exceptions.ProductNotFoundException;
import dev.nidhi.productservice.models.Category;
import dev.nidhi.productservice.models.Product;
import dev.nidhi.productservice.repositories.CategoryRepository;
import dev.nidhi.productservice.repositories.ProductRepository;
import dev.nidhi.productservice.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("ProductServiceDBImpl")
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "price","createdAt", "updatedAt");

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(Product product) {
        Category categoryEntity = getCategoryEntity(product);
        product.setCategory(categoryEntity);
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = productRepository.findAll();

        List<Product> products = productList
                .stream()
                .filter(product -> {
                    return product.getIsDeleted()==null
                    || product.getIsDeleted()==false;
                })
                .toList();
        return products;
    }

    public String deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return "Product with id " + id + " not found";
        }
        Product productEntity = product.get();
        productEntity.setIsDeleted(true);
        productRepository.save(productEntity);
        return "Product with id " + id + " deleted successfully";
    }

    public Product updateProduct(Long id, Product product) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty())
        {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }

        Product productEntity = optionalProduct.get();
        if(product.getTitle()!=null)
        {
            productEntity.setTitle(product.getTitle());
        }
        if(product.getDescription()!=null)
        {
            productEntity.setDescription(product.getDescription());
        }
        if(product.getPrice()!=null)
        {
            productEntity.setPrice(product.getPrice());
        }
        if(product.getImageUrl()!=null){
            productEntity.setImageUrl(product.getImageUrl());
        }
        if(product.getCategory()!=null){
            Category categoryEntity = getCategoryEntity(product);
            productEntity.setCategory(categoryEntity);
        }

        return productRepository.save(productEntity);
    }

    public Product getProductById(Long id) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty()){
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }

        return optionalProduct.get();
    }

    public Product replaceProduct(Long id, Product product) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty()){
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        product.setId(id);
        return createProduct(product);
    }

    public List<Product> getProductIDGreaterThan(Long id){
        return productRepository.productsGreaterThanID(id);
    }

    public List<Product> getProductWithCategoryNameEquals(String categoryName){
        return productRepository.productCategoryNameIsEqualTo(categoryName);
    }

    public List<Product> productAndCategoryJoinWhereCategoryName(String categoryName){
        return productRepository.productAndCategoryJoinWhereCategoryName(categoryName);
    }


    public Category getCategoryEntity(Product product){
        Optional<Category> category = categoryRepository.findByName(product.getCategory().getName());
        Category categoryEntity;
        if (category.isEmpty()) {
            categoryEntity = new Category();
            categoryEntity.setName(product.getCategory().getName());
            // this has to be done until we use cascadeType.Persist
            // but good practice is to write code manually and not use
            // cascade
            categoryEntity = categoryRepository.save(categoryEntity);
        }
        else {
            categoryEntity = category.get();
        }
        return categoryEntity;
    }

    public Page<Product> getProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProducts(int page, int size,
                                     List<String> sort,
                                     String search,
                                     Long categoryId,
                                     Double minPrice,
                                     Double maxPrice){

        if(page<0 || size>20 || size<1){
            throw new IllegalArgumentException("Page should be greater " +
                    "than 0 and size must between 1 and 20");
        }

        List<Sort.Order> orders = new ArrayList<>();

        for(String s : sort){
            String[] sortParts = s.split(",");

            String sortBy = sortParts[0];

            if(!ALLOWED_SORT_FIELDS.contains(sortBy)){
                throw new IllegalArgumentException("Sort field is invalid");
            }

            String direction = (sortParts.length>1)?sortParts[1]:"asc";

            orders.add(direction.equalsIgnoreCase("asc")?
                   Sort.Order.asc(sortBy):
                   Sort.Order.desc(sortBy));
        }

        Sort sortOrder  = Sort.by(orders);

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Specification<Product> specification = ProductSpecification.
                build(search, categoryId, minPrice, maxPrice);

      return productRepository.findAll(specification, pageable);
    }
}
