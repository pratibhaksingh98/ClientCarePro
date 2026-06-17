package com.project.ClientCarePro.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ClientCarePro.Modal.Customer;
import com.project.ClientCarePro.Modal.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>{

	Object countByCustomer(Customer customer);

}
