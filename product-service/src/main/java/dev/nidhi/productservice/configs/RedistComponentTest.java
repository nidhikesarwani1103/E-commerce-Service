package dev.nidhi.productservice.configs;

import dev.nidhi.productservice.dtos.ProductDTO;
import dev.nidhi.productservice.models.Product;
import dev.nidhi.productservice.services.ProductService;
import dev.nidhi.productservice.services.RedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

// To test the connection to Redis

@Component
public class RedistComponentTest implements ApplicationRunner {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisService redisService;
    private final ProductService productService;

    public RedistComponentTest(StringRedisTemplate stringRedisTemplate,
                               RedisService redisService, ProductService productService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisService = redisService;
        this.productService = productService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Redis Test started!");
        Product product1 = productService.getProductById(1L);
        redisService.save("PRODUCTS", "PRODUCTS:1",
                ProductDTO.fromProduct(product1));

        ProductDTO productDTO = redisService.get("PRODUCTS",
                                             "PRODUCTS:1",
                                                     ProductDTO.class);
        System.out.println(productDTO.toString());
    }


//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("Redis Test started!");
//        stringRedisTemplate.opsForValue().set("test-key","Hello, redis is set up");
//        String value = stringRedisTemplate.opsForValue().get("test-key");
//
//        System.out.println("Redis value: "+ value);
//    }


}
