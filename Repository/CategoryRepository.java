package com.project.ClientCarePro.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ClientCarePro.Modal.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	boolean existsByCategory(String cate);

}
