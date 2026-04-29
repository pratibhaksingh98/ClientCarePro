package com.project.ClientCarePro.Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.ClientCarePro.Dto.CustomerDto;
import com.project.ClientCarePro.Dto.ProductDto;
import com.project.ClientCarePro.Modal.Category;
import com.project.ClientCarePro.Modal.Complaint;
import com.project.ClientCarePro.Modal.Customer;
import com.project.ClientCarePro.Modal.Customer.Status;
import com.project.ClientCarePro.Modal.Enquiry;
import com.project.ClientCarePro.Modal.Feedback;
import com.project.ClientCarePro.Modal.NewsEvents;
import com.project.ClientCarePro.Modal.Product;
import com.project.ClientCarePro.Repository.CategoryRepository;
import com.project.ClientCarePro.Repository.ComplaintRepository;
import com.project.ClientCarePro.Repository.CustomerRepository;
import com.project.ClientCarePro.Repository.EnquiryRepository;
import com.project.ClientCarePro.Repository.FeedbackRepository;
import com.project.ClientCarePro.Repository.NewsEventsRepository;
import com.project.ClientCarePro.Repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin")
public class AdminController {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private EnquiryRepository enquiryRepo;

	@Autowired
	private FeedbackRepository feedbackRepo;

	@Autowired
	private ComplaintRepository complaintRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private NewsEventsRepository eventsRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private HttpSession session;

	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model) {
		
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		model.addAttribute("totalCustomers", customerRepo.count());
	    model.addAttribute("totalProducts", productRepo.count());
	    model.addAttribute("totalEnquiries", enquiryRepo.count());
	    model.addAttribute("totalComplaints", complaintRepo.count());
	    model.addAttribute("totalFeedback", feedbackRepo.count());
	    model.addAttribute("totalNewsEvents", eventsRepo.count());
	    model.addAttribute("session", session);
	    List<Enquiry> recentEnquiries = enquiryRepo.findTop3ByOrderByEnquirydateDesc();
	    model.addAttribute("recentEnquiries", recentEnquiries);
	    
		return "Admin/Dashboard";
	}
	
	//Active User Count
	@ModelAttribute("activeUserCount")
	 public int activeUserCount() {
	       return customerRepo.findByStatus(Status.ACTIVE).size();
	}
	 
	@GetMapping("/AddCustomer")
	public String ShowAddCustomer(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		model.addAttribute("customerDto", new CustomerDto());
		return "Admin/AddCustomer";
	}
	
	@PostMapping("/AddCustomer")
	public String AddCustomer(@ModelAttribute("customerDto") CustomerDto dto, RedirectAttributes attributes)
	{
		try {
			if (customerRepo.existsByEmail(dto.getEmail())) {
				attributes.addFlashAttribute("msg", "Customer Already Exists ⚠️");
				return "redirect:/Admin/AddCustomer";
			}
			
			Customer customer = new Customer();
			customer.setName(dto.getName());
			customer.setEmail(dto.getEmail());
			customer.setPhone(dto.getPhone());
			customer.setAddress(dto.getAddress());
			customer.setPassword(dto.getPassword());
			customer.setRole("CUSTOMER");
			customer.setStatus(Customer.Status.ACTIVE);
			customer.setCreatedAt(LocalDateTime.now());	
			customerRepo.save(customer);
			attributes.addFlashAttribute("msg", "Customer Successfully Added ✅");
			return "redirect:/Admin/AddCustomer";
		} catch (Exception e) {
			attributes.addFlashAttribute("error", "Error : "+e.getMessage());
			return "redirect:/Admin/AddCustomer";
		}
	}
	
	
	@GetMapping("/ViewAllCustomer")
	public String ShowViewAllCustomer(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
//		List<Customer> customers = customerRepo.findByStatus(Customer.Status.ACTIVE);
		List<Customer> customers = customerRepo.findAll();
		model.addAttribute("customers", customers);
		return "Admin/ViewAllCustomer";
	}
	
	@GetMapping("/DeleteCustomer")
	public String deleteCustomer(@RequestParam("id") Long id) {
	    customerRepo.deleteById(id);
	    return "redirect:/Admin/ViewAllCustomer";
	}

	@GetMapping("/EditCustomer")
	public String editCustomer(@RequestParam("id") Long id, Model model) {
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		Customer customer = customerRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
	    model.addAttribute("customer", customer);
	    return "Admin/EditCustomer";
	}
	
	@PostMapping("/UpdateCustomer")
	public String updateCustomer(@ModelAttribute("customer") Customer customerForm, RedirectAttributes attributes) {
	    try {
	    	
	    	Customer oldCustomer = customerRepo.findById(customerForm.getId()).get();
	    	oldCustomer.setEmail(customerForm.getEmail());
	    	oldCustomer.setName(customerForm.getName());
	    	oldCustomer.setAddress(customerForm.getAddress());
	    	oldCustomer.setStatus(customerForm.getStatus());
	    	oldCustomer.setPhone(customerForm.getPhone());
	    	customerRepo.save(oldCustomer);
	    	
	    	attributes.addFlashAttribute("msg", "Customer updated successfully!");
		    return "redirect:/Admin/ViewAllCustomer";
		} catch (Exception e) {
			attributes.addFlashAttribute("error", "Error : "+e.getMessage());
			return "redirect:/Admin/EditCustomer?id="+customerForm.getId();
		}
	}
	
	@GetMapping("/AddCategory")
	public String ShowAddCategory()
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		return "Admin/AddCategory";
	}
	
	@PostMapping("/AddCategory")
	public String AddCategory(@RequestParam("category") String cate, RedirectAttributes attributes)
	{
		try {
			if (categoryRepo.existsByCategory(cate)) {
				attributes.addFlashAttribute("error", "This category Already Exists ⚠️");
				return "redirect:/Admin/AddCategory";
			}
			Category category = new Category();
			category.setCategory(cate);
			categoryRepo.save(category);
			attributes.addFlashAttribute("success", "Category Successfully Added ✅");
			return "redirect:/Admin/AddCategory";
		} catch (Exception e) {
			attributes.addFlashAttribute("error", "Error : "+e.getMessage());
			return "redirect:/Admin/AddCategory";
		}
	}
	
	@GetMapping("/AddProduct")
	public String ShowAddProduct(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		model.addAttribute("productDto", new ProductDto());
		model.addAttribute("categories", categoryRepo.findAll());
		return "Admin/AddProduct";
	}
	
	@PostMapping("/AddProduct")
	public String AddProduct(@ModelAttribute("productDto") ProductDto dto,@RequestParam("imageFile") MultipartFile file, RedirectAttributes attributes)
	{
		try {
			String storageFileName = UUID.randomUUID()+"_"+file.getOriginalFilename();
			String uploadDir = "public/Products/";
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = file.getInputStream()){
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
			Product product = new Product();
			product.setName(dto.getName());
			product.setDescription(dto.getDescription());
			product.setPrice(dto.getPrice());
			product.setAvailabilityStatus(dto.getAvailabilityStatus());

			Category category = categoryRepo.findById(dto.getCategoryId()).orElse(null);
			product.setCategory(category);
			product.setImageUrl(storageFileName);
			
			
			productRepo.save(product);
			attributes.addFlashAttribute("msg", "Product Successfully Added ✅");
			return "redirect:/Admin/AddProduct";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/Admin/AddProduct";
		}
	}
	
	@GetMapping("/ViewProduct")
	public String viewAllProducts(Model model) {
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		List<Product> products = productRepo.findAll(); // or your service method
	    model.addAttribute("products", products);
	    return "Admin/ViewAllProduct";
	}
	
	@GetMapping("/DeleteProduct")
	public String DeleteProduct(@RequestParam("id") long id)
	{
		Product product = productRepo.findById(id).get();
		productRepo.delete(product);
		return "redirect:/Admin/ViewProduct";
	}
	
	@GetMapping("/AddNewsAndEvents")
	public String ShowNewsAndEvents(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		model.addAttribute("newsEvents", new NewsEvents());
		return "Admin/AddNewsAndEvents";
	}
	
	@PostMapping("/AddNewsAndEvents")
	public String saveNewsEvent(@ModelAttribute NewsEvents newsEvents,
	                            @RequestParam("imageFile") MultipartFile imageFile,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        // Handle image upload
	        if (!imageFile.isEmpty()) {
	            // Get the original file name
	            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

	            // Set upload path (change this path as needed)
	            String uploadDir = "public/NewsEvents/";
	            File uploadPath = new File(uploadDir);
	            if (!uploadPath.exists()) {
	                uploadPath.mkdirs();
	            }

	            // Save the file
	            Path path = Paths.get(uploadDir + fileName);
	            Files.write(path, imageFile.getBytes());

	            // Set image path to entity
	            newsEvents.setImageBanner(fileName);
	        }

	        // Set createdDate as today's date
	        newsEvents.setCreatedDate(LocalDateTime.now());

	        // Save to DB
	        eventsRepo.save(newsEvents);

	        redirectAttributes.addFlashAttribute("msg", newsEvents.getType()+" added successfully! ✅");
	        return "redirect:/Admin/AddNewsAndEvents";
	    } catch (IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("msg", "Error uploading image or saving data. ⚠️");
	        return "redirect:/Admin/AddNewsAndEvents";
	    }
	}

	@GetMapping("/ManageNewsAndEvents")
	public String manageNewsEvents(Model model) {
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		List<NewsEvents> list = eventsRepo.findAll();
	    model.addAttribute("newsEventsList", list);
	    return "Admin/ManageNewsAndEvents";
	}
	
	@GetMapping("/DeleteNewsEvent")
	public String DeleteNewsEvent(@RequestParam("id") long id)
	{
		NewsEvents newsEvents = eventsRepo.findById(id).get();
		eventsRepo.delete(newsEvents);
		return "redirect:/Admin/ManageNewsAndEvents";
	}
	
	@GetMapping("/ManageComplaints")
	public String ShowManageComplaints(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		model.addAttribute("complaints", complaintRepo.findAll());
		return "Admin/Complaint";
	}
	
	@GetMapping("/DeleteComplaint")
	public String DeleteComplaint(@RequestParam("id") long id)
	{
		Complaint complaint = complaintRepo.findById(id).get();
		complaintRepo.delete(complaint);
		return "redirect:/Admin/ManageComplaints";
	}
	
	@GetMapping("/ManageFeedback")
	public String ShowViewFeedback(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		model.addAttribute("feedbackList", feedbackRepo.findAll());
		return "Admin/ViewFeedback";
	}
	
	@GetMapping("/DeleteFeedback")
	public String DeleteFeedback(@RequestParam("id") long id)
	{
		Feedback feedback = feedbackRepo.findById(id).get();
		feedbackRepo.delete(feedback);
		return "redirect:/Admin/ManageFeedback";
	}
	
	@GetMapping("/Enquiries")
	public String ShowEnquiry(Model model)
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		
		model.addAttribute("enquiries", enquiryRepo.findAll());
		return "Admin/Enquiry";
	}
	
	@GetMapping("/DeleteEnquiry")
	public String DeleteEnquiry(@RequestParam("id") long id)
	{
		Enquiry enquiry = enquiryRepo.findById(id).get();
		enquiryRepo.delete(enquiry);
		return "redirect:/Admin/Enquiries";
	}
	
	@GetMapping("/ChangePassword")
	public String ShowChangePassword()
	{
		if (session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/AdminLogin";
		}
		return "Admin/ChangePassword";
	}
	
	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes)
	{
		try {
			String oldpass = request.getParameter("currentPassword");
			String newpass = request.getParameter("newPassword");
			String confirmpass = request.getParameter("confirmPassword");
			
			if (!newpass.equals(confirmpass)) {
				attributes.addFlashAttribute("msg", "Confirm Password and New Password not Matched");
				return "redirect:/Admin/ChangePassword";
			}
			
			Customer adminInfo = (Customer) session.getAttribute("loggedInAdmin");
			if (adminInfo.getPassword().equals(oldpass)) {
				adminInfo.setPassword(confirmpass);
				customerRepo.save(adminInfo);
				session.invalidate();
				attributes.addFlashAttribute("msg", "Password Successfully Changed ✅");
				return "redirect:/Admin/ChangePassword";
			}
			else {
				attributes.addFlashAttribute("msg", "Invalid Old Password ⚠️");
				return "redirect:/Admin/ChangePassword";
			}
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
			return "redirect:/Admin/ChangePassword";
		}
	}
	
	@GetMapping("/AdminLogout")
	public String Logout()
	{
		session.removeAttribute("loggedInAdmin");
		return "redirect:/login";
	}
	
}
