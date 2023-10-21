package fr.alten.productManager.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.alten.productManager.dto.ProductDTO;
import fr.alten.productManager.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductRest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final ProductService productService;

	public ProductRest(ProductService productService) {
		super();
		this.productService = productService;
	}
	
	@CrossOrigin//TODO: remove
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO product) throws URISyntaxException {
		log.debug("Received REST request createProduct {}", product);
		ProductDTO createdProduct = productService.add(product);
		return ResponseEntity.created(new URI("/products/"+createdProduct.getId()))
				.body(createdProduct);
	}

	@CrossOrigin//TODO: remove
	@PatchMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductDTO> updatePatchProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO product) throws URISyntaxException {
		log.debug("Received REST request updatePatchProduct {}", product);
		product.setId(id);
		ProductDTO updatedProduct = productService.updatePatch(product);
		if(updatedProduct==null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(updatedProduct);
	}

	@CrossOrigin//TODO: remove
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProductDTO>> getAll() {
		List<ProductDTO> products = productService.findAll(PageRequest.of(0, 9999)).getContent();
		return ResponseEntity.ok(products);
	}

	@CrossOrigin//TODO: remove
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
		Optional<ProductDTO> product = productService.findOne(id);
		if(product.isPresent())
			return ResponseEntity.ok(product.get());
		
		return ResponseEntity.notFound().build();
	}

	@CrossOrigin//TODO: remove
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
