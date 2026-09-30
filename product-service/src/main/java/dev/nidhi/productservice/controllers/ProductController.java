package dev.nidhi.productservice.controllers;

import dev.nidhi.productservice.dtos.ProductDTO;
import dev.nidhi.productservice.models.Product;
import dev.nidhi.productservice.services.ProductService;
import dev.nidhi.productservice.services.RedisService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final RedisService redisService;

    public ProductController(ProductService productService,
                             RedisService redisService) {
        this.productService = productService;
        this.redisService = redisService;
    }

    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = productService.createProduct(productDTO.toProduct());
        ProductDTO responseProductDTO = ProductDTO.fromProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(responseProductDTO);
    }

    @GetMapping("")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam MultiValueMap<String, String> params
            ) {

        List<String> sort = params.getOrDefault("sort", List.of("id,asc"));

        Page<Product> products = productService.getProducts(page, size,
                sort, search, categoryId, minPrice, maxPrice);

        Page<ProductDTO> productDTOS = products
                .map(product -> ProductDTO.fromProduct(product));

        return ResponseEntity.ok(productDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id,
                                    @RequestBody ProductDTO productDTO) {
        Product product = productService.updateProduct(id, productDTO.toProduct());
        return ResponseEntity.ok(ProductDTO.fromProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id){
        ProductDTO productDTO = redisService.
                                       get(
                                       "PRODUCTS",
                                       "PRODUCTS:"+id,
                                       ProductDTO.class);

        if(productDTO == null){
            System.out.println("Cache miss!");
            Product product = productService.getProductById(id);
            productDTO = ProductDTO.fromProduct(product);
            redisService.save("PRODUCTS", "PRODUCTS:"+id, productDTO);
        }

        return ResponseEntity.ok(productDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> replaceProduct(@RequestBody ProductDTO productDTO, @PathVariable("id") Long id){
        Product product = productService.replaceProduct(id, productDTO.toProduct());
        return ResponseEntity.ok(ProductDTO.fromProduct(product));
    }

    @GetMapping("/greater-than/{id}")
    public ResponseEntity<List<ProductDTO>> getProductIDGreaterThan(@PathVariable("id") Long id){
        List<ProductDTO> responseList = productService
                                            .getProductIDGreaterThan(id)
                                            .stream()
                                            .map(product -> ProductDTO.fromProduct(product))
                                            .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/category-equals-to/{categoryName}")
    public ResponseEntity<List<ProductDTO>> productCategoryNameIsEqualTo(@PathVariable("categoryName") String categoryName){
        List<ProductDTO> responseList = productService
                                            .getProductWithCategoryNameEquals(categoryName)
                                            .stream()
                                            .map(product -> ProductDTO.fromProduct(product))
                                            .toList();
        return ResponseEntity.ok(responseList);
    }


}
