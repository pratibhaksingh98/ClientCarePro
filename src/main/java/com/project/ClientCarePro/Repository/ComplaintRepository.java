package com.project.ClientCarePro.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ClientCarePro.Modal.Complaint;
import com.project.ClientCarePro.Modal.Customer;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>{

	Object countByCustomer(Customer customer);

}
