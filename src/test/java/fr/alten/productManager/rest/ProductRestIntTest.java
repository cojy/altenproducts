package fr.alten.productManager.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.productManager.domain.Product;
import fr.alten.productManager.dto.InventoryStatus;
import fr.alten.productManager.dto.ProductCategory;
import fr.alten.productManager.dto.ProductDTO;
import fr.alten.productManager.service.mapper.ProductMapper;
import fr.alten.productManager.service.repository.ProductRepository;
import utils.TestUtil;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRestIntTest {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ObjectMapper objectMapper;
	
    @Autowired
    private WebApplicationContext webAppContext;
    
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private static final String DEFAULT_CODE = "CODE1";
    private static final String UPDATED_CODE = "UPCO2";

    private static final String DEFAULT_NAME = "DEFNAME";
    private static final String UPDATED_NAME = "UPNAME";

    private static final String DEFAULT_DESCRIPTION = "DEFDESCR";
    private static final String UPDATED_DESCRIPTION = "UPDDESCR";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("12.23");
    private static final BigDecimal UPDATED_PRICE = new BigDecimal("36.50");

    private static final Float DEFAULT_QUANTITY = 15.2F;
    private static final Float UPDATED_QUANTITY = 3.56F;

    private static final InventoryStatus DEFAULT_STATUS = InventoryStatus.INSTOCK;
    private static final InventoryStatus UPDATED_STATUS = InventoryStatus.OUTOFSTOCK;

    private static final ProductCategory DEFAULT_CATEGORY = ProductCategory.Accessories;
    private static final ProductCategory UPDATED_CATEGORY = ProductCategory.Fitness;

    private static final String DEFAULT_IMAGE = "b64img1";
    private static final String UPDATED_IMAGE = "img2b64";
    
    private static final int DEFAULT_RATING = 1;
    private static final int UPDATED_RATING = 8;
    
    private Product product;
    
	private MockMvc mockMvc;
	
	@BeforeAll
	void init() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
	            .build();
	}
	
	@BeforeEach
	void prepare() {
		product = new Product();
		product.setCode(DEFAULT_CODE);
		product.setName(DEFAULT_NAME);
		product.setDescription(DEFAULT_DESCRIPTION);
		product.setPrice(DEFAULT_PRICE);
		product.setQuantity(DEFAULT_QUANTITY);
		product.setInventoryStatus(DEFAULT_STATUS);
		product.setCategory(DEFAULT_CATEGORY);
		product.setImage(DEFAULT_IMAGE);
		product.setRating(DEFAULT_RATING);
	}
	
	@AfterEach
	void cleanUp() {
		productRepository.deleteAll();
	}
	
	@Test
	void testProductGet() throws Exception {
		//PREPARE
		productRepository.save(product);
		
		//CALL
		MvcResult res = mockMvc.perform(get("/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	            .andExpect(jsonPath("$.id").isNotEmpty())
	            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
	            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
	            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
	            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
	            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
	            .andExpect(jsonPath("$.inventoryStatus").value(DEFAULT_STATUS.name()))
	            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.name()))
	            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE))
	            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andReturn();
		
		//VERIFY
		ProductDTO returnedProd = objectMapper.readValue(res.getResponse().getContentAsByteArray(), ProductDTO.class);
		//just make sure deserialize went ok
		assertEquals(DEFAULT_CODE, returnedProd.getCode());		
	}
	
	@Test
	void testProductDelete() throws Exception {
		//PREPARE
		productRepository.save(product);
		long dbSizeBefore = productRepository.count();
		
		//CALL
		mockMvc.perform(delete("/products/{id}", product.getId()))
				.andExpect(status().isNoContent());
		
		//VERIFY		
		assertEquals(dbSizeBefore-1, productRepository.count());
	}
	@Test
	void testProductDeleteNotExist() throws Exception {
		//PREPARE
		long dbSizeBefore = productRepository.count();
		
		//CALL
		mockMvc.perform(delete("/products/{id}", 36))
				.andExpect(status().isNoContent());
		
		//VERIFY		
		assertEquals(dbSizeBefore, productRepository.count());
	}
	
	@Test
	void testProductCreate() throws Exception {
		//PREPARE
		ProductDTO productDTO = productMapper.toDto(product);
		long dbSizeBefore = productRepository.count();
		
		//CALL
		MvcResult res = mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDTO)))
				//*
				.andExpect(status().isCreated())
	            .andExpect(jsonPath("$.id").isNotEmpty())
	            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
	            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
	            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
	            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
	            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
	            .andExpect(jsonPath("$.inventoryStatus").value(DEFAULT_STATUS.name()))
	            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.name()))
	            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE))
	            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))//*/
            .andReturn();
		
		//VERIFY
		ProductDTO returnedProd = objectMapper.readValue(res.getResponse().getContentAsByteArray(), ProductDTO.class);
		assertEquals(DEFAULT_CODE, returnedProd.getCode());
		
		List<Product> dbProducts = productRepository.findAll();
		assertEquals(dbSizeBefore+1, dbProducts.size());
		
		Product newProduct = dbProducts.get((int) dbSizeBefore);
		assertEquals(DEFAULT_CODE, newProduct.getCode());
		assertEquals(DEFAULT_NAME, newProduct.getName());
		assertEquals(DEFAULT_DESCRIPTION, newProduct.getDescription());
		assertEquals(DEFAULT_PRICE, newProduct.getPrice());
		assertEquals(DEFAULT_QUANTITY, newProduct.getQuantity());
		assertEquals(DEFAULT_STATUS, newProduct.getInventoryStatus());
		assertEquals(DEFAULT_CATEGORY, newProduct.getCategory());
		assertEquals(DEFAULT_IMAGE, newProduct.getImage());
		assertEquals(DEFAULT_RATING, newProduct.getRating());
	}
	
	@Test//front does not handle null values but sends some. Test we set a default value
	void testProductCreateMissingInventoryStatus() throws Exception {
		//PREPARE
		ProductDTO productDTO = productMapper.toDto(product);
		productDTO.setInventoryStatus(null);
		long dbSizeBefore = productRepository.count();
		
		//CALL
		MvcResult res = mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDTO)))
				//*
				.andExpect(status().isCreated())
	            .andExpect(jsonPath("$.id").isNotEmpty())
	            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
	            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
	            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
	            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
	            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
	            .andExpect(jsonPath("$.inventoryStatus").value(InventoryStatus.OUTOFSTOCK.name()))
	            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.name()))
	            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE))
	            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))//*/
            .andReturn();
		
		//VERIFY
		ProductDTO returnedProd = objectMapper.readValue(res.getResponse().getContentAsByteArray(), ProductDTO.class);
		assertEquals(DEFAULT_CODE, returnedProd.getCode());
		
		List<Product> dbProducts = productRepository.findAll();
		assertEquals(dbSizeBefore+1, dbProducts.size());
		
		Product newProduct = dbProducts.get((int) dbSizeBefore);
		assertEquals(DEFAULT_CODE, newProduct.getCode());
		assertEquals(DEFAULT_NAME, newProduct.getName());
		assertEquals(DEFAULT_DESCRIPTION, newProduct.getDescription());
		assertEquals(DEFAULT_PRICE, newProduct.getPrice());
		assertEquals(DEFAULT_QUANTITY, newProduct.getQuantity());
		assertEquals(InventoryStatus.OUTOFSTOCK, newProduct.getInventoryStatus());
		assertEquals(DEFAULT_CATEGORY, newProduct.getCategory());
		assertEquals(DEFAULT_IMAGE, newProduct.getImage());
		assertEquals(DEFAULT_RATING, newProduct.getRating());
	}
	
	@Test
	void testProductUpdateNoChange() throws Exception {
		//PREPARE
		ProductDTO productDTO = new ProductDTO();//all fields empty no change must happen
		productRepository.save(product);
		long dbSizeBefore = productRepository.count();
		
		//CALL
		MvcResult res = mockMvc.perform(patch("/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isOk())
            .andReturn();
		
		//VERIFY
		ProductDTO returnedProd = objectMapper.readValue(res.getResponse().getContentAsByteArray(), ProductDTO.class);
		assertEquals(DEFAULT_CODE, returnedProd.getCode());
		
		List<Product> dbProducts = productRepository.findAll();
		assertEquals(dbSizeBefore, dbProducts.size());
		
		Product newProduct = dbProducts.get((int) dbSizeBefore-1);
		assertEquals(DEFAULT_CODE, newProduct.getCode());
		assertEquals(DEFAULT_NAME, newProduct.getName());
		assertEquals(DEFAULT_DESCRIPTION, newProduct.getDescription());
		assertEquals(DEFAULT_PRICE, newProduct.getPrice());
		assertEquals(DEFAULT_QUANTITY, newProduct.getQuantity());
		assertEquals(DEFAULT_STATUS, newProduct.getInventoryStatus());
		assertEquals(DEFAULT_CATEGORY, newProduct.getCategory());
		assertEquals(DEFAULT_IMAGE, newProduct.getImage());
		assertEquals(DEFAULT_RATING, newProduct.getRating());
	}
	
	@Test
	void testProductUpdateChangeAll() throws Exception {
		//PREPARE
		productRepository.save(product);
		long dbSizeBefore = productRepository.count();
		
		ProductDTO productDTO = new ProductDTO();
		productDTO.setCode(UPDATED_CODE);
		productDTO.setName(UPDATED_NAME);
		productDTO.setDescription(UPDATED_DESCRIPTION);
		productDTO.setPrice(UPDATED_PRICE);
		productDTO.setQuantity(UPDATED_QUANTITY);
		productDTO.setInventoryStatus(UPDATED_STATUS);
		productDTO.setCategory(UPDATED_CATEGORY);
		productDTO.setImage(UPDATED_IMAGE);
		productDTO.setRating(UPDATED_RATING);
		
		//CALL
		MvcResult res = mockMvc.perform(patch("/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDTO)))
				.andExpect(status().isOk())
            .andReturn();
		
		//VERIFY
		ProductDTO returnedProd = objectMapper.readValue(res.getResponse().getContentAsByteArray(), ProductDTO.class);
		assertEquals(UPDATED_CODE, returnedProd.getCode());
		//TODO: test all fields
		
		List<Product> dbProducts = productRepository.findAll();
		assertEquals(dbSizeBefore, dbProducts.size());
		
		Product newProduct = dbProducts.get((int) dbSizeBefore-1);
		assertEquals(UPDATED_CODE, newProduct.getCode());
		assertEquals(UPDATED_NAME, newProduct.getName());
		assertEquals(UPDATED_DESCRIPTION, newProduct.getDescription());
		assertEquals(UPDATED_PRICE, newProduct.getPrice());
		assertEquals(UPDATED_QUANTITY, newProduct.getQuantity());
		assertEquals(UPDATED_STATUS, newProduct.getInventoryStatus());
		assertEquals(UPDATED_CATEGORY, newProduct.getCategory());
		assertEquals(UPDATED_IMAGE, newProduct.getImage());
		assertEquals(UPDATED_RATING, newProduct.getRating());
	}
	
	@Test
	void testProductUpdateNotExists() throws Exception {
		//PREPARE
		long dbSizeBefore = productRepository.count();
		
		//CALL
		mockMvc.perform(patch("/products/{id}", 85).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(new ProductDTO())))
			.andExpect(status().isNotFound())
            .andReturn();
		
		//VERIFY
		assertEquals(dbSizeBefore, productRepository.count());
	}
		
}
