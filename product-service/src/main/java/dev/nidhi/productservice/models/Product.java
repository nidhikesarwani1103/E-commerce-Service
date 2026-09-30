package dev.nidhi.productservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Product extends BaseModel{
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    @ManyToOne
    private Category category;
}
