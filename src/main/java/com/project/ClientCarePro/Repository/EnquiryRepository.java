package com.project.ClientCarePro.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.ClientCarePro.Modal.Enquiry;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long>{

	List<Enquiry> findTop3ByOrderByEnquirydateDesc();

}
