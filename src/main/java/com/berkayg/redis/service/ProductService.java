package com.berkayg.redis.service;

import com.berkayg.redis.domain.Product;
import com.berkayg.redis.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    // Anotasyonlarla Redis İşlemleri:
	/*@CachePut(value="products",key="#product.id")
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@CacheEvict(value="products", key="#id")
	public void delete(String id) {
		productRepository.deleteById(id);
	}

	@CachePut(value="products",key="#product.id")
	public Product update(Product product) {
		return productRepository.save(product);
	}

	@Cacheable(value="products")
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Cacheable(value="products", key = "#id")
	public Product findById(String id){
		return productRepository.findById(id).orElse(null);
	}*/

    private final RedisTemplate<String,Product> redisTemplate;
    private HashOperations<String, String, Product> hashOperations;
    private static final String KEY="Product";
    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }
    public Product save(Product product){
        productRepository.save(product);
        hashOperations.put(KEY,product.getId(),product);
        return product;
    }
    public void delete(String id){
        productRepository.deleteById(id);
        hashOperations.delete(KEY,id);
    }
    public Product update(Product product){
        productRepository.save(product);
        hashOperations.put(KEY,product.getId(),product);
        return product;
    }


    public Product findById(String id) {
        //ilk önce aranan ürün id'si cache'de var mı kontrol edilir.
        Product product = hashOperations.get(KEY,
                id);
        //eğer cache de yoksa:
        if(product == null){
            //veritabanında ürün aranır
            Optional<Product> optionalProduct = productRepository.findById(id);
            //eğer ürün veritabanında bulunursa
            if(optionalProduct.isPresent()){
                //ürünü get ile alırız.
                product = optionalProduct.get();
                //ardından cache'e yazarız.
                hashOperations.put(KEY,id,product);
            }
        }
        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = hashOperations.values(KEY);
        if (productList.isEmpty()) {
            productList = productRepository.findAll();
            //bu verileri cache'e yazalım:
            productList.forEach(product -> {
                hashOperations.put(KEY,product.getId(), product);
            });
        }
        return productList;
    }
}