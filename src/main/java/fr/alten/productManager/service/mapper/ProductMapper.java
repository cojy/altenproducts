package fr.alten.productManager.service.mapper;

import org.mapstruct.Mapper;

import fr.alten.productManager.domain.Product;
import fr.alten.productManager.dto.ProductDTO;

@Mapper
public interface ProductMapper {
	ProductDTO toDto(Product product);
	Product toEntity(ProductDTO productDTO);
}
