package dev.nidhi.productservice.models;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Setter
@Getter
@Entity
public class Category extends BaseModel{
    private String name;
  //  @Basic(fetch = FetchType.LAZY)
    private String description;
    @OneToMany()
    private List<Product> featuredProducts;
    @OneToMany(mappedBy = "category")
    private List<Product> AllProducts;

    private Integer productCount;
}
