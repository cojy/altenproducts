package fr.alten.productManager.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.alten.productManager.dto.ProductDTO;

public interface ProductService {

	ProductDTO add(ProductDTO productDTO);
	ProductDTO updatePatch(ProductDTO productDTO);
	void delete(Long productId);
	Optional<ProductDTO> findOne(Long productId);
	Page<ProductDTO> findAll(Pageable page);
}
