package com.project.ClientCarePro.Dto;

import com.project.ClientCarePro.Modal.Product.Availability;

public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private double price;
    private Long categoryId;
    private String imageUrl;
    private Availability availabilityStatus;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Availability getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Availability availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
