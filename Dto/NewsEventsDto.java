package com.project.ClientCarePro.Dto;

import java.time.LocalDate;

//NewsAndEventsDTO.java
public class NewsEventsDto {
 private Long id;
 private String title;
 private String description;
 private LocalDate eventDate;
 private LocalDate createdDate;
 
 private String imageBanner;
 
 private Type type;

 public enum Type {
     NEWS, EVENT
 }

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public LocalDate getEventDate() {
	return eventDate;
}

public void setEventDate(LocalDate eventDate) {
	this.eventDate = eventDate;
}

public LocalDate getCreatedDate() {
	return createdDate;
}

public void setCreatedDate(LocalDate createdDate) {
	this.createdDate = createdDate;
}

public String getImageBanner() {
	return imageBanner;
}

public void setImageBanner(String imageBanner) {
	this.imageBanner = imageBanner;
}

public Type getType() {
	return type;
}

public void setType(Type type) {
	this.type = type;
}
 
}