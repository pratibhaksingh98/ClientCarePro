package com.project.ClientCarePro.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ClientCarePro.Modal.Customer;
import com.project.ClientCarePro.Modal.Customer.Status;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	boolean existsByEmail(String username);

	Customer findByEmail(String username);

	List<Customer> findByStatus(Status active);

}
