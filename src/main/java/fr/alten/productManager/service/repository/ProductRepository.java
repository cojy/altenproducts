package fr.alten.productManager.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.alten.productManager.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
