package dev.nidhi.productservice.dtos;

import dev.nidhi.productservice.models.Category;
import dev.nidhi.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    private String category;

    public Product toProduct() {
        Product product = new Product();

        product.setTitle(this.title);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setImageUrl(this.imageUrl);
        product.setCategory(new Category());
        product.getCategory().setName(this.category);
        return product;
    }

    public static ProductDTO fromProduct(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setTitle(product.getTitle());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setCategory(product.getCategory().getName());
        return productDTO;
    }
}
