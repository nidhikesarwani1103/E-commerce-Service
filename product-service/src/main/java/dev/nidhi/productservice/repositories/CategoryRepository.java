package dev.nidhi.productservice.repositories;

import dev.nidhi.productservice.models.Category;
import dev.nidhi.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findByName(String name);
    Category save(Category category);
}
