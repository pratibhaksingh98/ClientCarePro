package com.project.ClientCarePro.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.ClientCarePro.Modal.Complaint;
import com.project.ClientCarePro.Modal.Customer;
import com.project.ClientCarePro.Modal.Customer.Status;
import com.project.ClientCarePro.Modal.Feedback;
import com.project.ClientCarePro.Modal.NewsEvents;
import com.project.ClientCarePro.Repository.ComplaintRepository;
import com.project.ClientCarePro.Repository.CustomerRepository;
import com.project.ClientCarePro.Repository.EnquiryRepository;
import com.project.ClientCarePro.Repository.FeedbackRepository;
import com.project.ClientCarePro.Repository.NewsEventsRepository;
import com.project.ClientCarePro.Repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Customer")
public class CustomerController {
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	FeedbackRepository feedbackRepo;
	
	@Autowired
	ComplaintRepository complaintRepo;
	
	@Autowired
	ProductRepository productRepo;
	
	@Autowired
	NewsEventsRepository eventsRepo;
	
	@Autowired
	EnquiryRepository enquiryRepo;
	
	@Autowired
	HttpSession session;
	
	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model)
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");

	    model.addAttribute("customer", customer);
	    model.addAttribute("totalComplaints", complaintRepo.countByCustomer(customer));
	    model.addAttribute("totalFeedbacks", feedbackRepo.countByCustomer(customer));
		model.addAttribute("session", session);
	    return "Customer/Dashboard";
	}
	
	@GetMapping("/SubmitFeedback")
	public String ShowSubmitFeedback(Model model)
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		model.addAttribute("feedback", new Feedback());
		return "Customer/SubmitFeedback";
	}
	
	@PostMapping("/SubmitFeedback")
	public String SubmitFeedback(@ModelAttribute("feedback") Feedback feedback, RedirectAttributes attributes)
	{
		try {
			Customer customer = (Customer) session.getAttribute("loggedInCustomer");
			feedback.setCustomer(customer);
			feedback.setFeedbackDate(LocalDate.now());
			feedbackRepo.save(feedback);
			attributes.addFlashAttribute("msg", "Feedback Succesfully Submitted ✅");
			return "redirect:/Customer/SubmitFeedback";
		} catch (Exception e) {
			return "redirect:/Customer/SubmitFeedback";
		}
	}
	
	
	@GetMapping("/SubmitComplaint")
	public String ShowSubmitComplaint(Model model)
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		model.addAttribute("complaint", new Complaint());
		return "Customer/SubmitComplaint";
	}
	
	@PostMapping("/SubmitComplaint")
	public String SubmitComplaint(@ModelAttribute("complaint") Complaint complaint, RedirectAttributes attributes)
	{
		try {
			Customer customer = (Customer) session.getAttribute("loggedInCustomer");
			complaint.setCustomer(customer);
			complaint.setComplaintDate(LocalDate.now());
			complaint.setStatus(Complaint.Status.PENDING);
			complaintRepo.save(complaint);
			attributes.addFlashAttribute("msg", "Complaint Successfully Submitted ✅");
			return "redirect:/Customer/SubmitComplaint";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/Customer/SubmitComplaint";
		}
	}
	
	@GetMapping("/ViewProducts")
	public String ShowViewProducts(Model model)
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		
		model.addAttribute("products", productRepo.findAll());
		return "Customer/ViewProducts";
	}
	
	@GetMapping("/ViewNewsEvents")
	public String ShowNewsAndEvents(Model model) {
		
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		model.addAttribute("newsEvents", eventsRepo.findAll());
		return "Customer/ViewNewsEvents";
	}
	
	@GetMapping("ViewMoreNewsEvent")
	public String viewNewsEvent(@RequestParam("id") Long id, Model model) {
		
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		
	    NewsEvents newsEvent = eventsRepo.findById(id)
	                            .orElseThrow(() -> new RuntimeException("News not found"));
	    model.addAttribute("newsEvent", newsEvent);
	    return "Customer/ViewMoreNewsEvent"; // matches viewnews.html
	}
	
	@GetMapping("/CustomerProfile")
	public String ShowViewProfile(Model model)
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		Customer loggedInCustomer = customerRepo.findById((long) 2).get();
		model.addAttribute("customer", loggedInCustomer);
		return "Customer/ViewProfile";
	}

	@GetMapping("/CustomerChangePassword")
	public String ShowChangePassword()
	{
		if (session.getAttribute("loggedInCustomer")==null) {
			return "redirect:/CustomerLogin";
		}
		return "Customer/ChangePassword";
	}
	
	@PostMapping("/CustomerChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes)
	{
		try {
			String oldpass = request.getParameter("currentPassword");
			String newpass = request.getParameter("newPassword");
			String confirmpass = request.getParameter("confirmPassword");
			
			if (!newpass.equals(confirmpass)) {
				attributes.addFlashAttribute("msg", "Confirm Password and New Password not Matched ❌");
				return "redirect:/Customer/CustomerChangePassword";
			}
			
			Customer customer = (Customer) session.getAttribute("loggedInCustomer");
			if (customer.getPassword().equals(oldpass)) {
				customer.setPassword(confirmpass);
				customerRepo.save(customer);
				session.invalidate();
				attributes.addFlashAttribute("msg", "Password Successfully Changed ✅");
				return "redirect:/CustomerLogin";
			}
			else {
				attributes.addFlashAttribute("msg", "Invalid Old Password ⚠️");
				return "redirect:/Customer/CustomerChangePassword";
			}
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/Customer/CustomerChangePassword";
		}
	}
	
	
	@GetMapping("/Logout")
	public String Logout()
	{
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		customer.setStatus(Status.INACTIVE);
		customerRepo.save(customer);
		session.removeAttribute("loggedInCustomer");
		return "redirect:/login";
	}
}
