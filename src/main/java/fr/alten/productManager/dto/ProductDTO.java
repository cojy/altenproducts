package fr.alten.productManager.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;

public class ProductDTO {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	
	private String code;
	private String name;
	private String description;
	private BigDecimal price;
	private Float quantity;
	private InventoryStatus inventoryStatus;
	private ProductCategory category;
	private String image;
	
	@Max(10)
	private Integer rating;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public InventoryStatus getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(InventoryStatus inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "ProductDTO [id=" + id + ", code=" + code + ", name=" + name + ", description=" + description
				+ ", price=" + price + ", quantity=" + quantity + ", inventoryStatus=" + inventoryStatus + ", category="
				+ category + ", image=" + image + ", rating=" + rating + "]";
	}	
}
