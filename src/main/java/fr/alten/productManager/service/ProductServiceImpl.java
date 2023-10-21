package fr.alten.productManager.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.alten.productManager.domain.Product;
import fr.alten.productManager.dto.InventoryStatus;
import fr.alten.productManager.dto.ProductDTO;
import fr.alten.productManager.service.mapper.ProductMapper;
import fr.alten.productManager.service.repository.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
	private final ProductRepository productRepository;
		
	public ProductServiceImpl(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}

	private ProductDTO save(ProductDTO productDTO) {
		Product entity = productMapper.toEntity(productDTO);
		Product savedProduct = productRepository.save(entity);
		return productMapper.toDto(savedProduct);
	}
	
	@Override
	public ProductDTO add(ProductDTO productDTO) {
		if(productDTO.getInventoryStatus()==null)
			productDTO.setInventoryStatus(InventoryStatus.OUTOFSTOCK);
		return this.save(productDTO);
	}

	@Override
	public ProductDTO updatePatch(ProductDTO productDTO) {
		Optional<ProductDTO> dbProductO = productRepository.findById(productDTO.getId())
				.map(productMapper::toDto);
		if(dbProductO.isEmpty())
			return null;
		
		ProductDTO dbProduct = dbProductO.get();
		
		Method[] gettersAndSetters = ProductDTO.class.getDeclaredMethods();
        //For each field existing in the DTO class, check if it has a value thanks to its getter
        //if a value is set, update the target DTO class with the value thanks to its setter
        //we could use direct field modification but this implies modifying access restrictions (field.setAccessible(true))
        //!!!Careful, does not work with inheritance!!!
		Arrays.stream(ProductDTO.class.getDeclaredFields()).forEach(f -> {
            String getterName = "get"+StringUtils.capitalize(f.getName());
            Arrays.stream(gettersAndSetters)
                .filter(method -> getterName.equals(method.getName()))
                .findFirst()
                .ifPresent(getterMethod -> {
                    try {
                        Object fieldValue = getterMethod.invoke(productDTO);
                        if(fieldValue != null) {
                            //retrieve the setter associated to this field
                            //if we found the getter, we will most likely find the setter, otherwise the catch will get us
                            String setterName = "set"+StringUtils.capitalize(f.getName());
                            //we could use java.beans.Statement, not sure which solution is best
                            Method method = ProductDTO.class.getMethod(setterName, f.getType());
                            method.invoke(dbProduct, fieldValue);
                        }
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        log.error("Could not set field {} when patching product", f, e);
                    }
                });
        });
		
		return this.save(dbProduct);
	}

	@Override
	public void delete(Long productId) {
		productRepository.deleteById(productId);		
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<ProductDTO> findOne(Long productId) {
		return productRepository.findById(productId)
				.map(productMapper::toDto);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ProductDTO> findAll(Pageable page) {
		return productRepository.findAll(page)
				.map(productMapper::toDto);
	}

}
