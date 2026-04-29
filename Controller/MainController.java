package com.project.ClientCarePro.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.ClientCarePro.Modal.Customer;
import com.project.ClientCarePro.Modal.Customer.Status;
import com.project.ClientCarePro.Modal.Enquiry;
import com.project.ClientCarePro.Repository.CustomerRepository;
import com.project.ClientCarePro.Repository.EnquiryRepository;
import com.project.ClientCarePro.Repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	ProductRepository productRepository;

	@Autowired
	EnquiryRepository enquiryRepository;
	
	
	@GetMapping("/")
	public String ShowIndex()
	{
		return "index";
	}
	
	@GetMapping("/login")
	public String ShowLogin()
	{
		return "login";
	}
	
	@GetMapping("/AdminLogin")
	public String ShowAdminLogin()
	{
		return "adminlogin";
	}
	
	@PostMapping("/AdminLogin")
	public String AdminLogin(HttpServletRequest request, RedirectAttributes attributes, HttpSession session)
	{
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if (!customerRepository.existsByEmail(username)) {
				attributes.addFlashAttribute("msg", "User does not Exists ❌");
				return "redirect:/AdminLogin";
			}
			
			Customer adminInfo = customerRepository.findByEmail(username);
			
			if (adminInfo.getPassword().equals(password) && adminInfo.getRole().equals("ADMIN")) {
				
				session.setAttribute("loggedInAdmin", adminInfo);
				return "redirect:/Admin/Dashboard";
			}
			else {
				attributes.addFlashAttribute("msg", "Invalid Password ⚠️");
			}	
			return "redirect:/AdminLogin";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/AdminLogin";
		}
	}
	
	@GetMapping("/AdminForgotPassword")
	public String ShowAdminForgotPassword()
	{
	    return "admin-forgot-password";
	}

	@PostMapping("/AdminForgotPassword")
	public String AdminForgotPassword(HttpServletRequest request, RedirectAttributes attributes)
	{
	    try {
	        String email = request.getParameter("email");
	        String newPassword = request.getParameter("newPassword");
	        String confirmPassword = request.getParameter("confirmPassword");

	        if (!customerRepository.existsByEmail(email)) {
	            attributes.addFlashAttribute("msg", "Admin email does not exist ❌");
	            return "redirect:/AdminForgotPassword";
	        }

	        Customer admin = customerRepository.findByEmail(email);

	        if (!admin.getRole().equals("ADMIN")) {
	            attributes.addFlashAttribute("msg", "This email is not admin ❌");
	            return "redirect:/AdminForgotPassword";
	        }

	        if (!newPassword.equals(confirmPassword)) {
	            attributes.addFlashAttribute("msg", "Password not matched ❌");
	            return "redirect:/AdminForgotPassword";
	        }

	        admin.setPassword(newPassword);
	        customerRepository.save(admin);

	        attributes.addFlashAttribute("msg", "Password reset successful ✅ Please login");
	        return "redirect:/AdminLogin";

	    } catch (Exception e) {
	        attributes.addFlashAttribute("msg", "Error : " + e.getMessage());
	        return "redirect:/AdminForgotPassword";
	    }
	}
	
	@GetMapping("/CustomerLogin")
	public String ShowCustomerLogin()
	{
		return "customerlogin";
	}
	
	@PostMapping("/CustomerLogin")
	public String CustomerLogin(HttpServletRequest request, RedirectAttributes attributes, HttpSession session)
	{
		try {
			String username = request.getParameter("email");
			String password = request.getParameter("password");
			
			if (!customerRepository.existsByEmail(username)) {
				attributes.addFlashAttribute("msg", "User does not Exists ❌");
				return "redirect:/CustomerLogin";
			}
			
			Customer customer = customerRepository.findByEmail(username);
			
			if (customer.getPassword().equals(password) && customer.getRole().equals("CUSTOMER")) {
				
					customer.setStatus(Status.ACTIVE);
					customerRepository.save(customer);
					session.setAttribute("loggedInCustomer", customer);
					return "redirect:/Customer/Dashboard";
			}
			else {
				attributes.addFlashAttribute("msg", "Invalid User or Password ⚠️");
			}	
			return "redirect:/CustomerLogin";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/CustomerLogin";
		}
	}
	
	@GetMapping("/CustomerRegister")
	public String ShowCustomerRegister(Model model)
	{
	    model.addAttribute("customer", new Customer());
	    return "customer-register";
	}

	@PostMapping("/CustomerRegister")
	public String SaveCustomer(@ModelAttribute("customer") Customer customer,
	RedirectAttributes attributes)
	{
	    try {

	        if(customerRepository.existsByEmail(customer.getEmail())){
	            attributes.addFlashAttribute("msg","Email already exists ❌");
	            return "redirect:/CustomerRegister";
	        }

	        customer.setRole("CUSTOMER");
	        customer.setStatus(Status.INACTIVE);

	        customerRepository.save(customer);

	        attributes.addFlashAttribute("msg",
	        "Registration successful ✅ Please Login");

	        return "redirect:/CustomerLogin";

	    }
	    catch(Exception e){
	        return "redirect:/CustomerRegister";
	    }
	}
	
	@GetMapping("/about")
	public String ShowAbout()
	{
		return "about";
	}
	
	@GetMapping("/services")
	public String ShowServices()
	{
		return "services";
	}
	
	@GetMapping("/contact")
	public String ShowContactUs(Model model)
	{
		model.addAttribute("enquiry", new Enquiry());
		return "contactus";
	}
	
	@PostMapping("/contact")
	public String SubmitEnquiry(@ModelAttribute("enquiry") Enquiry enquiry, RedirectAttributes attributes)
	{
		try {
			
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			enquiry.setEnquirydate(df.format(date));
			enquiry.setStatus("RESOLVED");
			enquiryRepository.save(enquiry);
			attributes.addFlashAttribute("msg", "We have received your Message ✅");
			return "redirect:/contact";
		} catch (Exception e) {
			return "redirect:/contact";
		}
	}
}
