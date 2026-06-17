package com.project.ClientCarePro.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.ClientCarePro.Modal.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
