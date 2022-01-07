package ca.mcgill.ecse.carshop.features;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.persistence.CarShopPersistence;
import ca.mcgill.ecse.carshop.model.*;
import ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.TOBusiness;
import ca.mcgill.ecse.carshop.application.CarShopApplication;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CucumberStepDefinitions {
	private static String filename = "testdata.carShop";
	
	private static CarShop carShop;
	private Owner myOwner;
	private static String error;
	private Business myBusiness;
	private TOBusiness toBusiness;
	private static int errorCntr;
	private static int pastAppointmentNum;
	private Appointment currApp=null;
	
	@Before
	public static void setUp() {
		CarShopPersistence.setFilename(filename);
		// remove test file
		File f = new File(filename);
		f.delete();
		// clear all data
		CarShopApplication.getCarShop().delete();
	}
	
	/**----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Scenario For: "Given ... Exist in the system"
	 * Shared Methods
	 * @author Shichang Zhang
	 * @author Junjian Chen
	 * @author Yuyan Shi
	 * @author John Wang
	 * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	@Given("a Carshop system exists")
	public void carShopExists() {
		carShop = CarShopApplication.getCarShop();
		error="";
		errorCntr=0;
	}
	
	@Given("an owner account exists in the system")
	public void ownerAccountExists() {
		if(myOwner==null) {
			myOwner=new Owner("owner","owner",carShop);
		}
	}
	
	 @Given("an owner account exists in the system with username {string} and password {string}")
		public void an_owner_account_exists_in_the_system_with_username_and_password(String string, String string2) {
			if(carShop.hasOwner()) {
				carShop.getOwner().setPassword(string2);
			} else {
				myOwner = new Owner(string, string2, carShop);
				carShop.setOwner(myOwner);
			}
		}
	
	@Given("a business exists in the system")
	public void businessExists() {
		myBusiness = new Business("","","","",carShop);
	}
	
	@Given("a business exists with the following information:")
	public void aBusinessExistsWithTheFollowingInformation(DataTable table) {
		List<Map<String,String>> rows = table.asMaps();
		for(Map<String,String> row : rows) {
			String name = row.get("name");
			String address = row.get("address");
			String phone = row.get("phone number");
			String email = row.get("email");
			myBusiness = new Business(name, address, phone, email, carShop);
		}
	}
	
	
	
	@Given("the business has the following opening hours:")
	public void theBusinessHasTheFollowingOpeningHours(DataTable table) {
		List<Map<String,String>> rows = table.asMaps();
		for(Map<String,String> row : rows) {
			DayOfWeek day = CarShopApplication.getDayOfWeekFromString(row.get("day"));
			Time startTime = Time.valueOf(row.get("startTime")+":00");
			Time endTime = Time.valueOf(row.get("endTime")+":00");
			BusinessHour businessHour = new BusinessHour(day,startTime,endTime,carShop);
			myBusiness.addBusinessHour(businessHour);
		}
	}
		
	@Given("the following customers exist in the system:")
	public void customersExist(DataTable a) {
		List<Map<String,String>> rows = a.asMaps();
		for(int i=0;i<rows.size();i++) {
			Map<String,String> row=rows.get(i);
			String username=row.get("username");
			String password=row.get("password");
			boolean usernameExist=false;
			for(Customer customer : carShop.getCustomers()) {
				if(customer.getUsername().equals(username)) {
					usernameExist = true;
					customer.setPassword(password);
					break;
				}
			}
			if(!usernameExist) {
				carShop.addCustomer(username, password);
			}
		}
	}
	
	@Given("the following technicians exist in the system:")
	public void techniciansExist(DataTable a) {
		List<Map<String,String>> rows = a.asMaps();
		for(int i=0;i<rows.size();i++) {
			Map<String,String> row=rows.get(i);
			String username=row.get("username");
			String password=row.get("password");
			TechnicianType techType=CarShopApplication.getTechnicianTypeFromString(row.get("type"));
			boolean usernameExist=false;
			for(Technician technician : carShop.getTechnicians()) {
				if(technician.getUsername().equals(username)) {
					usernameExist = true;
					technician.setPassword(password);
					break;
				}
			}
			if(!usernameExist) {
				carShop.addTechnician(username, password, techType);
			}
		}
	}
	

	@Given("the following services exist in the system:")
	public void servicesExist(DataTable a) {
		List<Map<String,String>> rows = a.asMaps();
		for(int i=0;i<rows.size();i++) {
			Map<String,String> row=rows.get(i);
			String name=row.get("name");
			int duration=Integer.parseInt(row.get("duration"));
			Garage myGarage=CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(row.get("garage")));
			Service myService=new Service(name,carShop, duration,myGarage);
		}
	}
	
	@Given("the following service combos exist in the system:")
	 public void serviceCombosExist(DataTable a) throws Exception {
		 List<Map<String,String>> rows = a.asMaps();
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				String comboName=row.get("name");
				String mainService=row.get("mainService");
				String services=row.get("services");
				String mandatoryList=row.get("mandatory");
				List<Boolean> inputMandatory=new ArrayList<Boolean>();
				CarShopApplication.setCurrentUser(myOwner);
				CarShopController.createCombo("owner",comboName, mainService, services, mandatoryList);
			}
			CarShopApplication.setCurrentUser(null);
	 }
	 
	 @Given("the system's time and date is {string}")
	 public void setDateAndTime(String dateAndTimeString) {
		 String[] dtArray=dateAndTimeString.split("\\+");
		 Date currentDate=Date.valueOf(dtArray[0]);
		 Time cuurentTime=Time.valueOf(dtArray[1]+":00");
		 carShop.setCurrentDate(currentDate);
		 carShop.setCurrentTime(cuurentTime);
	 }
	 
	 @Given("all garages has the following opening hours")
	 public void allGarageHasOH(DataTable a) {
		 List<Map<String,String>> rows = a.asMaps();
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				DayOfWeek day = CarShopApplication.getDayOfWeekFromString(row.get("day"));
				   Time startTime = Time.valueOf(row.get("startTime")+":00");
				   Time endTime = Time.valueOf(row.get("endTime")+":00");
				for(Garage g:CarShopApplication.getCarShop().getGarages()) {
					g.addBusinessHour(new BusinessHour(day, startTime, endTime, carShop));
				}
			}
	 }
//	  | startDate  | endDate    | startTime | endTime |
//    | 2021-02-25 | 2021-03-07 | 0:00      | 23:59   |
	 @Given("the business has the following holidays")
	 public void businessHasHoliday(DataTable a) {
		 List<Map<String,String>> rows = a.asMaps();
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				Date myStartDate=Date.valueOf(row.get("startDate"));
				Date myEndDate=Date.valueOf(row.get("endDate"));
				Time startTime = Time.valueOf(row.get("startTime")+":00");
				Time endTime = Time.valueOf(row.get("endTime")+":00");
				myBusiness.addHoliday(new TimeSlot(myStartDate, startTime, myEndDate, endTime, carShop));
			}
	 }
//	   | day       | startTime | endTime |
//     | Monday    | 9:00      | 17:00   |
//     | Tuesday   | 9:00      | 17:00   |
//     | Wednesday | 9:00      | 17:00   |
//     | Thursday  | 9:00      | 17:00   |
//     | Friday    | 9:00      | 15:00   |
	 @Given("the business has the following opening hours")
	 public void businessHasFollowingOH(DataTable a) {
		 List<Map<String,String>> rows = a.asMaps();
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				DayOfWeek day = CarShopApplication.getDayOfWeekFromString(row.get("day"));
				Time startTime = Time.valueOf(row.get("startTime")+":00");
				Time endTime = Time.valueOf(row.get("endTime")+":00");
				myBusiness.addBusinessHour(new BusinessHour(day, startTime, endTime, carShop));
			}
	 }
	 
	 @Given("the following appointments exist in the system:")
	 public void appointmentExist(DataTable a) {
		 List<Map<String,String>> rows = a.asMaps();
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				Customer myCustomer=(Customer)CarShopApplication.findUserFromUsername(row.get("customer"));
				ServiceCombo myCombo=CarShopApplication.findCombo(row.get("serviceName"));
				String[] optionalServiceString=row.get("optServices").split(",");
				List<Service> optionalService=new ArrayList<Service>();
				for(int j=0;j<optionalServiceString.length;j++) {
					optionalService.add(CarShopApplication.findService(optionalServiceString[j]));
				}
				String dateString=row.get("date");
				Date myDate=Date.valueOf(dateString);
				String[] timeSlots=row.get("timeSlots").split(",");
				List<Time> startTimeList=new ArrayList<Time>();
				List<Time> endTimeList=new ArrayList<Time>();
				for(int j=0;j<timeSlots.length;j++) {
					String[] startAndEndTime=timeSlots[j].split("-");
					startTimeList.add(Time.valueOf(startAndEndTime[0]+":00"));
					endTimeList.add(Time.valueOf(startAndEndTime[1]+":00"));
				}
				
				Appointment myApp=new Appointment(myCustomer, myCombo, carShop);
				myApp.addServiceBooking(myCombo.getMainService().getService(), new TimeSlot(myDate, startTimeList.get(0), myDate, endTimeList.get(0), carShop));
				for(int j=0;j<optionalService.size();j++) {
					myApp.addServiceBooking(optionalService.get(j), new TimeSlot(myDate, startTimeList.get(j+1), myDate, endTimeList.get(j+1), carShop));
				}
			}
	 }
	 /**
	  * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  * Scenario For: An account Log In
	  * Shared Methods
	  * @author Shichang Zhang
	  * @author Yuyan Shi
	  * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  */
	    @Given("the user with username {string} is logged in")
	    public void theUserWithUsernameIsLoggedIn(String username) {
	    	CarShopApplication.setCurrentUser(CarShopApplication.findUserFromUsername(username));
	    }
	    
	    @Given("the Owner with username {string} is logged in")
	    public void theOwnerWithUsernameIsLoggedIn(String username) {
	    	CarShopApplication.setCurrentUser(CarShopApplication.findUserFromUsername(username));
	    }
	    
	    @Given("the user is logged in to an account with username {string}")
	    public void theUserIsLoggedInToAnAccountWithUsername(String username) {
	    	User user = CarShopApplication.findUserFromUsername(username);
	    	CarShopApplication.setCurrentUser(user);
	    }
	    
	    @Given("{string} is logged in to their account")
	    public void customerLoggedIn(String username) {
	    	User user = CarShopApplication.findUserFromUsername(username);
	    	CarShopApplication.setCurrentUser(user);
	    }
	    /**
		  * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
		  * Other Shared Methods
		  * Shared Methods
		  * @author Shichang Zhang
		  * @author Junjian Chen
		  * @author Yuyan Shi
		  * @author John Wang
		  * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
		  */
	    @Then("an error message with content {string} shall be raised")
		 public void thereIsAnErrorSaying(String errorMsg) {
				assertTrue(error.contains(errorMsg));
		}
	    
	    @Then("an error message {string} shall be raised")
		 public void AnErrorMessageShallBeRaised(String errorMsg) {
				assertTrue(error.contains(errorMsg));
		}
	    
	    @Given("each technician has their own garage")
		public void ownGarage() {
			for(Technician t:carShop.getTechnicians()) {
				if(t.hasGarage()==false) {
					carShop.addGarage(t);
				}
			}
		}
	    	    
	    /**
	     * tearDown: Delete the current carShop after one iteration
	     * Shared Method
	     */
	    @After
		 public void tearDown() {
	    	CarShopApplication.setCurrentUser(null);
	    	CarShopApplication.setLoginState(false);
			 carShop.delete();
			 currApp=null;
		 }
	    
	    
//----------------------------------------------------------------------------------------------------------------------------------------------------------
	    /**--------------------------------------------------------------------------------------------------------------------------------------
	     * Feature 1:Sign up for customer account/Update customer account
	     * @author Yuyan Shi
	     *---------------------------------------------------------------------------------------------------------------------------------------
	     */
	    
	    @Given("there is no existing username {string}")
		public void there_is_no_existing_username(String string) {
			List<Customer> customers = carShop.getCustomers();
			for(int i = 0; i < customers.size(); i++) {
				Customer customer = customers.get(i);
				if(string.equals(customer.getUsername())) {
					carShop.removeCustomer(customer);
					break;
				}
			}
		}
	    
	    @When("the user provides a new username {string} and a password {string}")
		public void the_user_provides_a_new_username_and_a_password(String string, String string2) {
			try {
				CarShopController.signUp(string, string2);
			} catch(InvalidInputException e) {
				error += e.getMessage();
				errorCntr++;
			}
		}
 
	    @Then("a new customer account shall be created")
		public void a_new_customer_account_shall_be_created() {
	    	System.out.println(error);
			assertEquals(CarShopApplication.getNumOfAccount(), 1+CarShopApplication.getOldNumOfAccount());
		}
	    
	    @Then("no new account shall be created")
		public void no_new_account_shall_be_created() {
	    	assertEquals(CarShopApplication.getNumOfAccount(), CarShopApplication.getOldNumOfAccount());
		}
	    
	    @Given("there is an existing username {string}")
		public void there_is_an_existing_username(String string) {
	    	User user = CarShopApplication.findUserFromUsername(string);
	    	String [] words = string.split("-");
	    	if(user == null) {
	    		if(string.equals("owner")) {
	    			myOwner = new Owner(string, string, carShop);
	    		}else if (words.length==2 && CarShopApplication.checkType(words[0]) && words[1].equals("Technician")) {
	    			carShop.addTechnician(string, string, CarShopApplication.getTechnicianTypeFromString(words[0]));
	    		}else {
					carShop.addCustomer(string, string);
	    		}
	    	} 
/*			if(string.contentEquals("owner")) {
				if(!carShop.hasOwner()) {
					myOwner = new Owner(string, string, carShop);
					carShop.setOwner(myOwner);
				}
			} else if(string.contains("Technician")) {
				List<Technician> technicians = carShop.getTechnicians();
				Boolean containTech = false;
				for(int j = 0; j < technicians.size(); j++) {
					Technician technician = technicians.get(j);
					if(technician.getUsername().contentEquals(string)) {
						containTech = true;
						break;
					}
				}
				if(containTech == false) {
					carShop.addTechnician(string, string, null);
				}
			} else {
				List<Customer> customers = carShop.getCustomers();
				Boolean containUsername = false;
				for(int i = 0; i < customers.size(); i++) {
					Customer customer = customers.get(i);
					if(string.contentEquals(customer.getUsername())) {
						containUsername = true;
						break;
					}
				}
				if(containUsername == false) {
					carShop.addCustomer(string, string);
				}
			}
*/		}
	    
	    
	    @When("the user tries to update account with a new username {string} and password {string}")
		public void the_user_tries_to_update_account_with_a_new_username_and_password(String string, String string2) {
			try {
				CarShopController.updateAccount(string, string2);
			} catch(InvalidInputException e) {
				error += e.getMessage();
				errorCntr++;
			}
		}
	    
	    @Then("the account shall not be updated")
		public void the_account_shall_not_be_updated() {
	    	assertFalse(CarShopApplication.getAccountUpdatedState());
		}
	    
	    
	    /**--------------------------------------------------------------------------------------------------------------------------------------
	     * Feature 2:Login as customer,technician,or owner/Update garage opening hours
	     * @author 
	     *---------------------------------------------------------------------------------------------------------------------------------------
	     */
	    
	    /**
	     * Login related steps
	     *	@author Shichang Zhang
	     */
	    
	    @When("the user tries to log in with username {string} and password {string}")
	    public void theUserTriesToLogInWithUsernameAndPassword (String username, String password) {
	    	try{
	    		CarShopController.logIn(username, password);
	    	}catch(InvalidInputException e) {
	    		error += e.getMessage();
				errorCntr++;
	    	}
	    }
	    
	   @Then("the user should be successfully logged in")
	   public void theUserShouldBeSuccessfullyLoggedIn() {
		   assertTrue(CarShopApplication.getLoginState());
	   }
	   
	   @Then("the user should not be logged in")
	   public void theUserShouldNotBeLoggedIn () {
		   assertFalse(CarShopApplication.getLoginState());
	   }

	   
	   @Then("a new account shall be created")
	   public void aNewAccountShallBeCreated() {
		   assertEquals(CarShopApplication.getNumOfAccount(),1+CarShopApplication.getOldNumOfAccount());
	   }
	   
	   @Then("the user shall be successfully logged in")
	   public void theUserShallBeSuccessfullyLoggedIn() {
		   assertTrue(CarShopApplication.getLoginState());
	   }
	   
	   @Then("the account shall have username {string} and password {string}")
	   public void theAccountShallHaveUsernameAndPassword(String usernameCorrect, String passwordCorrect) {
		   assertEquals(usernameCorrect, User.getWithUsername(usernameCorrect).getUsername());
		   //assertTrue(User.hasWithUsername(usernameCorrect));
		   assertEquals(User.getWithUsername(usernameCorrect).getPassword(), passwordCorrect);
	   }
	   
	   @Then("the account shall have username {string}, password {string} and technician type {string}")
	   public void theAccountShallHaveUsernamePasswordAndTechnicianType (String usernameCorrect, String passwordCorrect, String typeCorrect ) {
		   //assertTrue(User.hasWithUsername(usernameCorrect));
		   assertEquals(usernameCorrect, User.getWithUsername(usernameCorrect).getUsername());
		   Technician technician = (Technician)User.getWithUsername(usernameCorrect);
		   assertEquals(technician.getPassword(), passwordCorrect);
		   assertEquals(technician.getType(),CarShopApplication.getTechnicianTypeFromString(typeCorrect));
	   }
	   
	   @Then("the corresponding garage for the technician shall be created")
	   public void theCorrespondingGarageForTheTechnicianShallBeCreated () {
		   boolean flag=false;
		   User user =CarShopApplication.getCurrentUser();
		   if( user instanceof Technician) {
			   Technician technician = (Technician) user;
			   if(technician.getGarage()!=null) flag =true;
		   }
		   assertTrue(flag);
	   }
	   
	   @Then("the garage should have the same opening hours as the business")
	   public void theGarageShouldHaveTheSameOpeningHoursAsTheBusiness() {
		   boolean flag=false;
		   User user =CarShopApplication.getCurrentUser();
		   if( user instanceof Technician) {
			   Technician technician = (Technician) user;
			   Garage garage = technician.getGarage();
			   if(garage!=null) {
				   int counter = 0;
				   for(BusinessHour businessHour1 : garage.getBusinessHours()) {
					   for(BusinessHour businessHour2 : carShop.getBusiness().getBusinessHours()) {
						   if(businessHour1.equals(businessHour2)) {
							   counter++;
							   break;
						   }
					   }
				   }
				   if(counter==garage.getBusinessHours().size()) flag = true;
			   }
		   }
		   assertTrue(flag);
	   }
	   
	   
	   /**
	     * garage opening hours related steps
	     *	@author Shichang Zhang
	   */
	   
	   @When("the user tries to add new business hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	   public void theUserTriesToAddNewBusinessHour(String dayString, String startTimeString, String endTimeString, String typeString) {
		   try {
			   CarShopController.addGarageOpeningHour(CarShopApplication.getCurrentUser().getUsername(), dayString, startTimeString, endTimeString, typeString);
		   }catch(InvalidInputException e) {
			   error += e.getMessage();
				errorCntr++;
		   }
	   }
	   
	   @Then("the garage belonging to the technician with type {string} should have opening hours on {string} from {string} to {string}")
	   public void theGarageBelongingToTheTechnicianWithTypeShouldHaveOpeningHoursOn(String typeCorrect, String dayCorrect, String startTimeCorrect, String endTimeCorrect) {
		   Garage garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(typeCorrect));
		   DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayCorrect);
		   Time startTime = Time.valueOf(startTimeCorrect+":00");
		   Time endTime = Time.valueOf(endTimeCorrect+":00");
		   boolean flag = false;
		   for (BusinessHour businessHour: garage.getBusinessHours()) {
			   if(businessHour.getDayOfWeek().equals(day) && businessHour.getStartTime().equals(startTime) && businessHour.getEndTime().equals(endTime)) {
				   flag = true;
			   } 
		   }
		   assertTrue(flag);
	   }
	   
	   @Given("there are opening hours on {string} from {string} to {string} for garage belonging to the technician with type {string}")
	   public void thereAreOpeningHoursForGarage(String dayCorrect, String startTimeCorrect, String endTimeCorrect, String typeCorrect) {
		   Garage garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(typeCorrect));
		   DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayCorrect);
		   Time startTime = Time.valueOf(startTimeCorrect+":00");
		   Time endTime = Time.valueOf(endTimeCorrect+":00");
		   BusinessHour businessHour = new BusinessHour(day,startTime, endTime, carShop);
		   garage.addBusinessHour(businessHour);
	   }
	   
	   @When("the user tries to remove opening hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	   public void theUserTriesToRemoveBusinessHour(String dayString, String startTimeString, String endTimeString, String typeString) {
		   try {
			   CarShopController.removeGarageOpeningHour(CarShopApplication.getCurrentUser().getUsername(), dayString, startTimeString, endTimeString, typeString);
		   }catch(InvalidInputException e) {
			   error += e.getMessage();
				errorCntr++;
		   }   
	   }
	   
	   @Then("the garage belonging to the technician with type {string} should not have opening hours on {string} from {string} to {string}")
	   public void theGarageBelongingToTheTechnicianWithTypeShouldNotHaveOpeningHoursOn(String typeCorrect, String dayCorrect, String startTimeCorrect, String endTimeCorrect) {
		   Garage garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(typeCorrect));
		   DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayCorrect);
		   Time startTime = Time.valueOf(startTimeCorrect+":00");
		   Time endTime = Time.valueOf(endTimeCorrect+":00");
		   boolean flag = false;
		   for (BusinessHour businessHour: garage.getBusinessHours()) {
			   if(businessHour.getDayOfWeek().equals(day) && businessHour.getStartTime().equals(startTime) && businessHour.getEndTime().equals(endTime)) {
				   flag = true;
			   } 
		   }
		   assertFalse(flag);
	   }
	   
	   
	   
	   
	   
	    /**--------------------------------------------------------------------------------------------------------------------------------------
	     * Feature 3:Setup Business Information/Update service combo
	     * @author John Wang
	     *---------------------------------------------------------------------------------------------------------------------------------------
	    */
	   	
	   @Given("no business exists")
	    public void no_business_exists() {
	        // Write code here that turns the phrase above into concrete actions
	        if (carShop.hasBusiness()) {
	            carShop.getBusiness().delete();
	        }
	    }
	   
	   
//	   @Given("the system's time and date is {string}")
//	    public void the_system_s_time_and_date_is(String string) {
//	        // Write code here that turns the phrase above into concrete actions
//	    	String[] dateAndTime = string.split("\\+");
//	    	String date = dateAndTime[0]; // yyyy-mm-dd
//	    	String time = dateAndTime[1]; // hh:mm
//	    	CarShopApplication.setCurrentDate(date);
//	    	CarShopApplication.setCurrentTime(time);
//	    }
	   
	   @When("the user tries to set up the business information with new {string} and {string} and {string} and {string}")
	    public void the_user_tries_to_set_up_the_business_information_with_new_and_and_and(String name, String address, String phoneNumber, String email) {
	    	// When the user tries to set up the business information with new "<name>" and "<address>" and "<phone number>" and "<email>"
	        // Write code here that turns the phrase above into concrete actions
	    	try {
	    		CarShopController.setupBusinessInformation(name, address, phoneNumber, email);
	    	}catch(Exception e) {
	    		error += e.getMessage();
	    		errorCntr++;
	    	}   	
	        
	    }
	   
	   @Then("a new business with new {string} and {string} and {string} and {string} shall {string} created")
	    public void a_new_business_with_new_and_and_and_shall_created(String name, String address, String phoneNumber, String email, String beOrNotBe) {
	    	//a new business with new "<name>" and "<address>" and "<phone number>" and "<email>" shall "<result>" created
	        // Write code here that turns the phrase above into concrete actions
	    	if (beOrNotBe.equals("be")) {
	    		assertTrue(name.equals(carShop.getBusiness().getName()));
	    		assertTrue(address.equals(carShop.getBusiness().getAddress()));
	    		assertTrue(phoneNumber.equals(carShop.getBusiness().getPhoneNumber()));
	    		assertTrue(email.equals(carShop.getBusiness().getEmail()));
	    	}
	    	if (beOrNotBe.equals("not be")) {
	    		assertTrue(carShop.getBusiness() == null);
	    	}
	    }
	   
	   @Given("the business has a business hour on {string} with start time {string} and end time {string}")
	    public void the_business_has_a_business_hour_on_with_start_time_and_end_time(String date, String start,String end) {
	        // Write code here that turns the phrase above into concrete actions
	    	start += ":00";
	    	end += ":00";
	    	BusinessHour businessHour = new BusinessHour(CarShopApplication.getDayOfWeekFromString(date), Time.valueOf(start), Time.valueOf(end), CarShopApplication.getCarShop());
	    	carShop.getBusiness().addBusinessHour(businessHour);
	        //System.out.println(carShop.getBusiness().getBusinessHours().size());
	        //System.out.println("--------------");
	    }
	   
	   @When("the user tries to add a new business hour on {string} with start time {string} and end time {string}")
	    public void the_user_tries_to_add_a_new_business_hour_on_with_start_time_and_end_time(String day, String start, String end) {
	        // Write code here that turns the phrase above into concrete actions
	    	try {
	    		CarShopController.addBusinessHour(day, start, end);
	    	}catch(InvalidInputException e) {
	    		error += e.getMessage();
	    		errorCntr++;
	    	}
	    }
	   
	   @Then("a new business hour shall {string} created")
	    public void a_new_business_hour_shall_created(String beOrNotBe) {
	        // Write code here that turns the phrase above into concrete actions
/*	    	if (beOrNotBe.equals("be")) { // there should be a businessHour
	    		assertEquals(2, carShop.getBusiness().getBusinessHours().size());
	    	}
	    	if (beOrNotBe.equals("not be")) {
	    		assertEquals(1, carShop.getBusiness().getBusinessHours().size());
	    	}
*/	   
		   boolean state = CarShopApplication.getBusinessHourUpdateState();
		   if(beOrNotBe.equals("be")) {
			   assertTrue(state);
		   }else if (beOrNotBe.equals("not be")) {
			   assertFalse(state);
		   }
	   }
	   
	   @When("the user tries to access the business information")
	    public void the_user_tries_to_access_the_business_information() {
	        // Write code here that turns the phrase above into concrete actions
	    		toBusiness= CarShopController.ViewBusinessInfo(CarShopApplication.getCurrentUser());
	    }
	    
	   @Then("an error message {string} shall {string} raised")
	    public void an_error_message_shall_raised(String errorMsg, String beOrNotBe) {
	        // Write code here that turns the phrase above into concrete actions
		   if (beOrNotBe.equals("be")) {
	    		assertTrue(error.contains(errorMsg));
	    	}
	    	
	    	if (beOrNotBe.equals("not be")) {
	    		assertTrue("".equals(error) && "".equals(errorMsg));
	    	}
	    }
	   
	   @Then("the {string} and {string} and {string} and {string} shall be provided to the user")
	    public void the_and_and_and_shall_be_provided_to_the_user(String name, String address, String phoneNumber, String email) {
	        // Write code here that turns the phrase above into concrete actions
	    	assertEquals(name,toBusiness.getName());
	    	assertEquals(address,toBusiness.getAddress());
	    	assertEquals(phoneNumber,toBusiness.getPhoneNumber());
	    	assertEquals(email,toBusiness.getEmail());
	    }
	   
	   @Given("a {string} time slot exists with start time {string} at {string} and end time {string} at {string}")
	    public void a_time_slot_exists_with_start_time_at_and_end_time_at(String type, String startDateString, String startTimeString, String endDateString, String endTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        Date startDate = Date.valueOf(startDateString);
			Date endDate = Date.valueOf(endDateString);

			Time startTime = Time.valueOf(startTimeString + ":00");
			Time endTime = Time.valueOf(endTimeString + ":00");
			
			TimeSlot timeSlot = new TimeSlot(startDate, startTime, endDate, endTime, carShop);
	        if ("holiday".equals(type)) {
	            carShop.getBusiness().addHoliday(timeSlot);
	        } else if("vacation".equals(type)){
	            carShop.getBusiness().addVacation(timeSlot);
	        }
	    }
	   
	   @When("the user tries to add a new {string} with start date {string} at {string} and end date {string} at {string}")
	    public void the_user_tries_to_add_a_new_with_start_date_at_and_end_date_at(String type, String startDateString, String startTimeString, String endDateString, String endTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        try {
	            CarShopController.addTimeSlot(type, startDateString, startTimeString, endDateString, endTimeString);
	    	}catch(InvalidInputException e) {
	    		error += e.getMessage();
	    		errorCntr++;
	    	}
	    }
	   
	   @Then("a new {string} shall {string} be added with start date {string} at {string} and end date {string} at {string}")
	    public void a_new_shall_be_added_with_start_date_at_and_end_date_at(String type, String result, String startDateString, String startTimeString, String endDateString, String endTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        Date startDate = Date.valueOf(startDateString);
			Date endDate = Date.valueOf(endDateString);

			Time startTime = Time.valueOf(startTimeString + ":00");
			Time endTime = Time.valueOf(endTimeString + ":00");
			
	        boolean isFound = false;
	        
	        if ("holiday".equals(type)) {            
                for (TimeSlot timeslot: carShop.getBusiness().getHolidays()) {
                    if (timeslot.getStartDate().equals(startDate) 
                    && timeslot.getStartTime().equals(startTime) 
                    && timeslot.getEndDate().equals(endDate)
                    && timeslot.getEndTime().equals(endTime)) {
                        isFound = true;
                        // assertTrue(true);
                    }
                }
            } else if ("vacation".equals(type)){
                for (TimeSlot timeslot: carShop.getBusiness().getVacations()) {
                    if (timeslot.getStartDate().equals(startDate) 
                    && timeslot.getStartTime().equals(startTime) 
                    && timeslot.getEndDate().equals(endDate)
                    && timeslot.getEndTime().equals(endTime)) {
                        isFound = true;
                    }        
                }
            }
	        if (result.equals("be")) {	            
	            assertTrue(isFound);
	    	}else if (result.equals("not be")) {
	            assertFalse(isFound);
	    	} 
	    }
	   
	   
	   @When("the user tries to update the business information with new {string} and {string} and {string} and {string}")
	    public void the_user_tries_to_update_the_business_information_with_new_and_and_and(String name, String address, String phoneNumber, String email) {
	        // Write code here that turns the phrase above into concrete actions
	        try {
	            CarShopController.updateBusinessInformation(name, address, phoneNumber, email);
	        }catch (InvalidInputException e) {
	            error += e.getMessage();
	            errorCntr++;
	        }
	    }
	   
	   @Then("the business information shall {string} updated with new {string} and {string} and {string} and {string}")
	    public void the_business_information_shall_updated_with_new_and_and_and(String beOrNotBe, String name, String address, String phoneNumber, String email) {
	        // Write code here that turns the phrase above into concrete actions
	       boolean flag = false;
	       if(name.equals(carShop.getBusiness().getName()) && address.equals(carShop.getBusiness().getAddress()) && phoneNumber.equals(carShop.getBusiness().getPhoneNumber()) && email.equals(carShop.getBusiness().getEmail())) {
	    	   flag = true;
	       }
		   if (beOrNotBe.equals("be")) {
			   assertTrue(flag);
	    	} else if (beOrNotBe.equals("not be")) {
	    		assertFalse(flag);
	    	} 
	    }
	    
	   @When("the user tries to change the business hour {string} at {string} to be on {string} starting at {string} and ending at {string}")
	    public void the_user_tries_to_change_the_business_hour_at_to_be_on_starting_at_and_ending_at(String curDate, String curStartTimeString, String targetDate, String targetStartTimeString, String targetEndTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        try {
	            CarShopController.updateExistingBusinessHours(curDate, curStartTimeString, targetDate, targetStartTimeString, targetEndTimeString);
	        }catch (InvalidInputException e) {
	            error += e.getMessage();
	            errorCntr++;
	        }
	    }
	   
	   @Then("the business hour shall {string} be updated")
	    public void the_business_hour_shall_be_updated(String beOrNotBe) {
	        // Write code here that turns the phrase above into concrete actions
	        boolean state = CarShopApplication.getBusinessHourUpdateState();
/*	        for (BusinessHour b: CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
	            if (b.getDayOfWeek() == CarShopApplication.getDayOfWeekFromString("ThursDay") && b.getStartTime().equals(Time.valueOf("09:00:00"))) {
	                isFound = true;
	            }
	        }	        
*/	    	
	        System.out.println(error);
	        if (beOrNotBe.equals("be")) { // there should be a businessHour
	            assertTrue(state);
	    	}
	    	if (beOrNotBe.equals("not be")) {
	            assertFalse(state);
	    	}
	    }
	   
	   @When("the user tries to remove the business hour starting {string} at {string}")
	    public void the_user_tries_to_remove_the_business_hour_starting_at(String curDate, String curStartTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        try {
	            CarShopController.removeExistingBusinessHours(curDate, curStartTimeString);
	        }catch (InvalidInputException e) {
	            error += e.getMessage();
	            errorCntr++;
	        }
	        
	    }
	   
	   @Then("the business hour starting {string} at {string} shall {string} exist")
	    public void the_business_hour_starting_at_shall_exist(String dateString, String startTimeString, String result) {
	        // Write code here that turns the phrase above into concrete actions
	        boolean isFound = false;
	        for (BusinessHour b: CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
	            if (b.getDayOfWeek() == CarShopApplication.getDayOfWeekFromString(dateString) && b.getStartTime().equals(Time.valueOf(startTimeString + ":00"))) {
	                isFound = true;
	            }
	        }
	    	if (result.equals("not")) { // there should be a businessHour
	            assertFalse(isFound);
	    	}
	    	if (result.equals("")) {
	            assertTrue(isFound);
	    	}
	    }
	   
	   @Then("an error message {string} shall {string} be raised")
	    public void an_error_message_shall_be_raised(String errorString, String result) {
	        // Write code here that turns the phrase above into concrete actions
	        if ("not".equals(result)) {
	            assertTrue("".equals(error));  
	        }
	        if ("".equals(result)) {
	            assertTrue(errorString.equals(error)); 
	        } 
	    }
	   
	   @When("the user tries to change the {string} on {string} at {string} to be with start date {string} at {string} and end date {string} at {string}")
	    public void the_user_tries_to_change_the_on_at_to_be_with_start_date_at_and_end_date_at(String type, String curStartDateString, String curStartTimeString, String targetStartDateString, String targetStartTimeString, String targetEndDateString, String targetEndTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        try {
	            if (type.equals("vacation")) {
	                CarShopController.updateVacation(type, curStartDateString, curStartTimeString, targetStartDateString, targetStartTimeString, targetEndDateString, targetEndTimeString);
	            }
	            if (type.equals("holiday")) {
	                CarShopController.updateHoliday(type, curStartDateString, curStartTimeString, targetStartDateString, targetStartTimeString, targetEndDateString, targetEndTimeString);
	            }
	        } catch(InvalidInputException e) {
	            error += e.getMessage();
	            errorCntr++;
	        }
	    }
	   
	   
	// Then the "vacation" shall "<result>" updated with start date "<startDate>" at "<startTime>" and end date "<endDate>" at "<endTime>"
	    @Then("the {string} shall {string} updated with start date {string} at {string} and end date {string} at {string}")
	    public void the_shall_updated_with_start_date_at_and_end_date_at(String type, String result, String startDateString, String startTimeString, String endDateString, String endTimeString) {
	        // Write code here that turns the phrase above into concrete actions
	        Date startDate = Date.valueOf(startDateString);
			Date endDate = Date.valueOf(endDateString);
			Time startTime = Time.valueOf(startTimeString + ":00");
			Time endTime = Time.valueOf(endTimeString + ":00");
			
	        boolean isFound = false;
	        if ("holiday".equals(type)) {
                for (TimeSlot timeslot: carShop.getBusiness().getHolidays()) {
                    if (timeslot.getStartDate().equals(startDate) 
                    	&& timeslot.getStartTime().equals(startTime) 
                    	&& timeslot.getEndDate().equals(endDate)
                    	&& timeslot.getEndTime().equals(endTime)) {
                        	isFound = true;
                    }
                }
            } else {
                for (TimeSlot timeslot: carShop.getBusiness().getVacations()) {
                    if (timeslot.getStartDate().equals(startDate) 
                    	&& timeslot.getStartTime().equals(startTime) 
                    	&& timeslot.getEndDate().equals(endDate)
                    	&& timeslot.getEndTime().equals(endTime)) {
                        	isFound = true;
                    }
                }
            }
	        
	        if (result.equals("be")) {	      
	            assertTrue(isFound);
	    	}else if (result.equals("not be")) {
	            assertFalse(isFound);
	    	} 

	    }
	   
	 // When the user tries to remove an existing "<type>" with start date "<startDate>" at "<startTime>" and end date "<endDate>" at "<endTime>"
	    @When("the user tries to remove an existing {string} with start date {string} at {string} and end date {string} at {string}")
	    public void the_user_tries_to_remove_an_existing_with_start_date_at_and_end_date_at(String type, String startDateString, String startTimeString, String endDateString, String endTimeString) {
	        try {
	            CarShopController.removeExistingTimeSlot(type, startDateString, startTimeString, endDateString, endTimeString);
	        } catch(InvalidInputException e) {
	            error += e.getMessage();
	            errorCntr++;
	        } 
	    }
	   
	 // Then the "<type>" with start date "<startDate>" at "<startTime>" shall "<result>" exist
	    // Then an error message "No permission to update business information" shall "<result>" be raised
	    @Then("the {string} with start date {string} at {string} shall {string} exist")
	    public void the_with_start_date_at_shall_exist(String type, String startDateString, String startTimeString, String result) {
	        // Write code here that turns the phrase above into concrete actions
	        boolean isFound = false;
	        List<TimeSlot> timeSlots = carShop.getBusiness().getHolidays();

	        if (type.equals("vacation")) {
	            timeSlots = carShop.getBusiness().getVacations();
	        }
	        
	        Date targetStartDay = Date.valueOf(startDateString);
			Time targetStart = Time.valueOf(startTimeString+ ":00");

	        for (TimeSlot v: timeSlots) {
	            if (v.getStartDate().equals(targetStartDay) && v.getStartTime().equals(targetStart)) {
	                isFound = true;
	            }
	        }	
	        
	    	if (result.equals("not")) { // there should be a businessHour
	            assertTrue(!isFound);
	    	} else if (result.equals("")) {
	            assertTrue(isFound);
	    	}
	    }
	    
	    
	    
	    /**--------------------------------------------------------------------------------------------------------------------------------------
	     * Feature 4: Add/Update Service
	     * @author Shichang Zhang
	     *---------------------------------------------------------------------------------------------------------------------------------------
	     */
	
		 @When("{string} initiates the addition of the service {string} with duration {string} belonging to the garage of {string} technician")
			public void addServiceWithName(String username, String serviceName, String duration, String type){
		    	//Garage garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(type));
		    	//Service service = new Service(serviceName,carShop,duration.intValue(),garage);
		    	try {
		    		CarShopController.addService(serviceName, Integer.parseInt(duration), type);
		    	}catch(InvalidInputException e) {
		    		error += e.getMessage();
					errorCntr++;
		    	}
		    }
		 
		 @Then("the service {string} shall exist in the system")
		    public void theServiceShallExistInTheSystem(String name) throws Exception {
		    	ArrayList<String> list = new ArrayList<String>();
		    	for(BookableService service : CarShopApplication.getCarShop().getBookableServices()) {
		    		list.add(service.getName());
		    	}
		    	assertTrue(list.contains(name));
		    }
		 
		 @Then("the service {string} shall belong to the garage of {string} technician")
		    public void theServiceShallBelongToTheGarageOf(String name,String type){
		    	Service service=null;
		    	service = (Service)CarShopApplication.findServiceWithName(name);
		    	assertEquals(service.getGarage().getTechnician().getType(),CarShopApplication.getTechnicianTypeFromString(type));
		    }
		 
		 @Then("the number of services in the system shall be {string}")
		    public void theNumberOfServicesInTheSystemShallBe(String numberCorrect) {
		    	int number=0;
		    	for(BookableService bookableService :  CarShopApplication.getCarShop().getBookableServices()) {
		    		if(bookableService instanceof Service){
		    			number++;
		    		}
		    	}
		    	assertEquals(Integer.parseInt(numberCorrect),number);
		    }
		 
		 @Then("the service {string} shall still preserve the following properties:")
		    public void theSerViceShallStillPreServeTheFollowingProperties(String name, DataTable table) {
		    	Service service = (Service)CarShopApplication.findServiceWithName(name);
		    	String nameCorrect="";
		    	int durationCorrect=0;
		    	TechnicianType typeCorrect=null;
		    	List<Map<String, String>> info = table.asMaps();
		    	for (Map<String, String> row : info) {
					nameCorrect = row.get("name");
					durationCorrect = Integer.parseInt(row.get("duration"));
					typeCorrect = CarShopApplication.getTechnicianTypeFromString(row.get("garage"));
		    	}
		    	assertEquals(service.getName(),nameCorrect);
		    	assertEquals(service.getDuration(),durationCorrect);
		    	assertEquals(service.getGarage().getTechnician().getType(),typeCorrect);
		    }
		    
		    @Then("the service {string} shall not exist in the system")
		    public void theServiceShallNotExistInTheSystem(String name) {
		    	ArrayList<String> list = new ArrayList<String>();
		    	for(BookableService service :  CarShopApplication.getCarShop().getBookableServices()) {
		    		list.add(service.getName());
		    	}
		    	assertFalse(list.contains(name));
		    }
		    
		   
		    
		    @When("{string} initiates the update of the service {string} to name {string}, duration {string}, belonging to the garage of {string} technician")
		    public void updateServiceWithName(String username, String serviceName, String name, String duration, String type) {
		    	try {
		    		CarShopController.updateService(serviceName, name, Integer.parseInt(duration), type);
		    	}catch(InvalidInputException e) {
		    		error += e.getMessage();
					errorCntr++;	
		    	}
		    }
		    
		    @Then("the service {string} shall be updated to name {string}, duration {string}")
		    public void theServiceShallBeUpdatedTo(String serviceName, String nameCorrect, String durationCorrect) {
		    	Service updatedService = (Service)CarShopApplication.findServiceWithName(nameCorrect);
		    	int correct = 0;
		    	if(updatedService != null) {
		    		if(updatedService.getDuration()==Integer.parseInt(durationCorrect)) {
		    			correct =1;
		    		}
		    	}
		    	assertTrue(correct==1);
		    }

	/**--------------------------------------------------------------------------------------------------------------------------------------
    * Feature 5: Define/Update Service Combo
	* @author Junjian Chen
	*---------------------------------------------------------------------------------------------------------------------------------------
    */
	
	//-------------Scenario Outline: Define a service combo successfully-----------------------------
	@When("{string} initiates the definition of a service combo {string} with main service {string}, services {string} and mandatory setting {string}")
	public void ownerInitialization(String username, String comboName, String mainService, String services, String mandatory) throws Exception {
		try {
				CarShopController.createCombo(username, comboName,mainService, services, mandatory);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}
	
	@Then("the service combo {string} shall exist in the system")
	public void serviceComboShallExist(String comboName) {
		assertTrue(CarShopApplication.findCombo(comboName) != null);
	}
	
	@Then("the service combo {string} shall contain the services {string} with mandatory setting {string}")
	public void serviceComboShallContainServicesWithMandatory(String comboName,String services, String mandatorySetting) throws Exception {
		ServiceCombo myCombo=CarShopApplication.findCombo(comboName);
		String[] serviceList=services.split(",");
		String[] mandatroyList=mandatorySetting.split(",");
		ComboItem comparedService=null;
		boolean result = true;
		for(int i=0;i<myCombo.getServices().size();i++) {
			comparedService=myCombo.getServices().get(i);
			if(!comparedService.getService().getName().equals(serviceList[i])) {
				result = false;
				break;
			}
			if(comparedService.getMandatory()!=(mandatroyList[i].equals("true"))) {
				
				result= false;
				break;
			}
		}		
		assertTrue(result);
	}
		
	@Then("the main service of the service combo {string} shall be {string}")
	public void serviceComboShallHavemainService(String comboName,String mainService) {
		ServiceCombo myCombo=CarShopApplication.findCombo(comboName);
		String actualMainService=myCombo.getMainService().getService().getName();
		String expectMainService=mainService;
		assertEquals(expectMainService,actualMainService);
	}
		
	
	 @Then("the service {string} in service combo {string} shall be mandatory")
	 public void mainServiceShallBeMandatory(String mainService,String comboName) {
		 ServiceCombo myCombo=CarShopApplication.findCombo(comboName);
		 List<ComboItem> itemList=myCombo.getServices();
		 ComboItem main = null;
		 for(int i=0;i<itemList.size();i++) {
			 if(itemList.get(i).getService().getName().equals(mainService)) {
				 main=itemList.get(i);
				 break;
			 }
		 }
		 if(main==null) {
			 assertTrue(false);
		 }else {
			 assertTrue(main.getMandatory());
		 }
		
	 }
	 
	 @Then("the number of service combos in the system shall be {string}")
	 public void numOfCombosShallBe1(String number) {
		 int num=Integer.parseInt(number);
		 assertEquals(CarShopApplication.numOfCombo(),num);
	 }
	 
	//-------------Scenario Outline: Define a service combo with invalid parameters----------------------------- 
	 @Then("the service combo {string} shall not exist in the system")
	 public void serviceComboShallNotExist(String comboName) {
			assertTrue(CarShopApplication.findCombo(comboName) == null);
		}
	 
	 @Then("the service combo {string} shall preserve the following properties:")
	 public void serviceComboPreserveProperties(String comboName,DataTable a) {
		 List<Map<String,String>> rows = a.asMaps();
		 boolean result = true;
			for(int i=0;i<rows.size();i++) {
				Map<String,String> row=rows.get(i);
				ServiceCombo myCombo=CarShopApplication.findCombo(comboName);
				if(!comboName.equals(row.get("name"))) {
					result=false;
				}
				if(!myCombo.getMainService().getService().getName().equals(row.get("mainService"))) {
					result=false;
					break;
				}
				String[] service=row.get("services").split(",");
				for(int j=0;j<service.length;j++) {
					if(!service[j].equals(myCombo.getServices().get(j).getService().getName())) {
						result=false;
						break;
					}
				}
				String[] mandatoryList=row.get("mandatory").split(",");
				for(int j=0;j<mandatoryList.length;j++) {
					if(myCombo.getService(j).getMandatory()!=(mandatoryList[j].equals("true"))) {
						result = false;
						break;
					}
				}
			}
		assertTrue(result);
	 }
	//------------- Scenario Outline: Unauthorized attempt to define a service combo-----------------------------
	
	 
	 @When("{string} initiates the update of service combo {string} to name {string}, main service {string} and services {string} and mandatory setting {string}")
	 public void ownerUpdateAServiceCombo(String username,String oldName, String newName,String mainService, String services,String mandatory) throws Exception {		
			//Create the Combo-----------------------------------------------------
			try {
					CarShopController.updatingCombo(username,oldName,newName,mainService, services, mandatory);
			} catch (Exception e) {
				error += e.getMessage();
				errorCntr++;
			}	 
	 }
	 
	 @Then("the service combo {string} shall be updated to name {string}")
	 public void serviceComboNameShallBeUpdated(String oldName, String newName) {
		 ServiceCombo oldCombo=CarShopApplication.findCombo(oldName);
		 ServiceCombo newCombo=CarShopApplication.findCombo(newName);
		 boolean result=(oldCombo==(null)||!(newCombo==(null)));
		 assertTrue(result);
	 }
	 
	 /**--------------------------------------------------------------------------------------------------------------------------------------
	 * Feature 6:Make appointment/Cancel Appointment
	 * @author Junjian Chen
	 * @author Shichang Zhang 
	 *---------------------------------------------------------------------------------------------------------------------------------------
	 */
	 
	//appointment of a service
		 @When("{string} schedules an appointment on {string} for {string} at {string}")
		 public void customerScheduleAnAppointment(String customer, String date, String serviceName, String startTime) throws Exception {
			 pastAppointmentNum=carShop.getAppointments().size();				
			try {
				CarShopController.makeAppointment(customer, date, serviceName, startTime);
			}catch(InvalidInputException e) {
				error+=e.getMessage();
				errorCntr++;
			}	 
		 }
		 
		 
		 @Then("{string} shall have a {string} appointment on {string} from {string} to {string}")
		 public void shallHaveApp(String customer,String serviceName,String date,String startTime, String endTime) throws Exception {
			 
			 boolean result= true;
			 Customer myCustomer=CarShopApplication.findCustomer(customer);
			 BookableService myBookableService= CarShopApplication.findBookableService(serviceName);
			 Date d=Date.valueOf(date);
			 
			 Appointment a=CarShopApplication.findAppByCustomerAndBookableServiceAndDate(myCustomer, myBookableService,d);
			 if(!a.getBookableService().getName().equals(serviceName)) {
				 result=false;
			 }

			 String[] startTimeString=startTime.split(",");
			 String[] endTimeString=endTime.split(",");
			 
			 for(int i=0;i<startTimeString.length;i++) {
				 startTimeString[i]=startTimeString[i]+":00";
				 endTimeString[i]=endTimeString[i]+":00";
			 }
			 
			 for(int i=0;i<startTimeString.length;i++) {
				 if(a.getServiceBooking(i).getTimeSlot().getStartTime().compareTo(Time.valueOf(startTimeString[i]))!=0) {
					 result = false;
				 }
				 if(a.getServiceBooking(i).getTimeSlot().getEndTime().compareTo(Time.valueOf(endTimeString[i]))!=0) {
					 result = false;

					
				 }
				 if(a.getServiceBookings().get(i).getTimeSlot().getStartDate().compareTo(d)!=0) {
					 result = false;
				 }
			 }	 
			 assertTrue(result);
			 
		 }
		
		 @Then("there shall be {int} more appointment in the system")
		 public void numMoreApp(int number) {
			 assertEquals(number,carShop.getAppointments().size()-pastAppointmentNum);
		 }
		 
		//appointment of a service combo
		 
		 @When("{string} schedules an appointment on {string} for {string} with {string} at {string}") 
		 public static void customerScheduleAnComboAppointment(String customer,String date,String serviceName,String optionalServices, String startTimes) throws Exception {
			 	pastAppointmentNum=carShop.getAppointments().size();				
				try {
					CarShopController.makeComboAppointment(customer, date, serviceName, optionalServices, startTimes);
				}catch(InvalidInputException e) {
					error+=e.getMessage();
					errorCntr++;				
				}
		 }
		 
		 @Then("the system shall report {string}")
		 public void the_system_shall_report(String string) {
			 System.out.println(string);
			 System.out.println(error);
			 assertTrue(error.contains(string));
		 }
		 
		 @When("{string} attempts to cancel their {string} appointment on {string} at {string}") 
		 public void customerAttemptsToCancelApp(String username, String serviceName, String date, String startTime) {
			 pastAppointmentNum=carShop.getAppointments().size();
			 try {
				CarShopController.cancelAppointment(username, username,date, serviceName, startTime);
			} catch (InvalidInputException e) {
				error+=e.getMessage();
				errorCntr++;
			}
		 }
		 
		 @Then("{string}'s {string} appointment on {string} at {string} shall be removed from the system") 
		 public void shallRemoved(String username,String serviceName, String date,String startTime) {
			 Customer myCustomer=CarShopApplication.findCustomer(username);
			 BookableService myBS=CarShopApplication.findBookableService(serviceName);
			 Date myDate=Date.valueOf(date);
			 Time t = Time.valueOf(startTime+":00");
			 Appointment a=CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(myCustomer, myBS, myDate,t);
			 assertEquals(a,null);
		 }
		 
		 @Then("there shall be {int} less appointment in the system") 
		 public void numLessApp(int number) {
			 assertEquals(number,pastAppointmentNum-carShop.getAppointments().size());
		 }
		 
		

		 @Then("{string} shall have a {string} appointment on {string} at {string} with the following properties")
		 public void shall_have_a_appointment_on_at_with_the_following_properties(String username, String serviceName, String date, String startTime,DataTable a) {
			 Customer myCustomer=CarShopApplication.findCustomer(username);
			 BookableService myBS=CarShopApplication.findBookableService(serviceName);
			 Date myDate=Date.valueOf(date);
			 List<Service> optServices=new ArrayList<Service>();
			 List<Time> startTimes=new ArrayList<Time>();
			 List<Time> endTimes=new ArrayList<Time>();
			 List<Map<String,String>> rows = a.asMaps();
			for(Map<String,String> row : rows) {
			     String[] optString=row.get("optServices").split(",");
			     for(int i=0;i<optString.length;i++) {
			    	 optServices.add(CarShopApplication.findService(optString[i]));
			     }
			     String[] startAndEnd=row.get("timeSlots").split(",");
			     for(int i=0;i<startAndEnd.length;i++) {
			    	 String[] subStartAndEnd=startAndEnd[i].split("-");
			    	 startTimes.add(Time.valueOf(subStartAndEnd[0]+":00"));
			    	 endTimes.add(Time.valueOf(subStartAndEnd[1]+":00"));
			     }
				
			 }
			
			boolean result=true;
			Appointment a1=CarShopApplication.findAppByCustomerAndBookableServiceAndDate(myCustomer, myBS, myDate);
					
			if(a1==null) {
				result=false;
			}else {
				for(int j=0;j<startTimes.size();j++) {
					if(a1.getServiceBookings().get(j).getTimeSlot().getStartTime().compareTo(startTimes.get(j))!=0) {
						result=false;
					}
					if(a1.getServiceBookings().get(j).getTimeSlot().getEndTime().compareTo(endTimes.get(j))!=0) {
						result=false;
					}
				}
				if(a1.getBookableService() instanceof ServiceCombo) {
					ServiceCombo myCombo=(ServiceCombo)a1.getBookableService();
					if(!myCombo.getName().equals(serviceName)) {
						result = false;
					}
					for(int j=0;j<optServices.size();j++) {
						if(!optServices.get(j).getName().equals(a1.getServiceBooking(j+1).getService().getName())) {
							result = false;
						}
					}
				}else {
					
				}
				
			}
			assertTrue(result);
		 }
		 
		 @When("{string} attempts to cancel {string}'s {string} appointment on {string} at {string}")
		 public void otherCancelCustomerApp(String username, String customer,String serviceName,String date, String startTime) {
			 pastAppointmentNum=carShop.getAppointments().size();
			try {
				CarShopController.cancelAppointment(username, customer,date, serviceName, startTime);
			}catch(Exception e) {
				error+=e.getMessage();
				errorCntr++;
			}
		 }
		 
/**
 * -------------------------------------------------------------------------------------------------------------------
 * Deliverable 3 Feature: AppointmentManagement
 * @author Shichang Zhang
 * @author John
 * @author Yuyan Shi
 * @author Junjian Chen
 * -------------------------------------------------------------------------------------------------------------------
 */
		 
		 /**
		  * @author Junjian Chen
		  */
		 @Given("{string} has {int} no-show records")
		 public void has_no_show_records(String string, Integer int1) {
		     Customer customer = CarShopApplication.findCustomer(string);
		     customer.setTimeNotShow(int1);
		 }
		 
		 /**
		  * @author Shichang Zhang, Junjian Chen
		  */
		 @When("{string} makes a {string} appointment for the date {string} and time {string} at {string}")
		 public void makes_a_appointment_for_the_date_and_time_at(String user, String service, String serviceDate, String startTime, String appointmentDateAndTime) throws Exception {
		     try {
		    	 pastAppointmentNum=carShop.getAppointments().size();
		    	 String curDate=appointmentDateAndTime.split("\\+")[0];
		    	String curTime=appointmentDateAndTime.split("\\+")[1];
		    	carShop.setCurrentDate(Date.valueOf(curDate));
		    	carShop.setCurrentTime(Time.valueOf(curTime+":00"));
		    	currApp = null;
		    	currApp=CarShopController.makeAppointment(user, serviceDate,service, startTime);  	 
		     }catch(RuntimeException e){
		    	error+=e.getMessage();
				errorCntr++;
		     }
		 }
		 
		 /**
		  * @author Shichang Zhang, Junjian Chen
		 * @throws Exception 
		  */
		 @When("{string} attempts to change the service in the appointment to {string} at {string}")
		 public void attempts_to_change_the_service_in_the_appointment_to_at(String user, String newService, String appointmentDateAndTime) throws Exception {
			 
		     try {
		    	 String curDate=appointmentDateAndTime.split("\\+")[0];
				 String curTime=appointmentDateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
			     if(currApp!=null) {
			    	 TimeSlot ts = currApp.getServiceBookings().get(0).getTimeSlot();
					 CarShopController.updateServiceOfAppointment(currApp, user, newService,ts.getStartDate().toString(),ts.getStartTime().toString());				
				 } 
		     }catch(RuntimeException e){
		    	error+=e.getMessage();
				errorCntr++;
		     }
		 }

		 /**
		  * @author Shichang Zhang
		  */
		 @Then("the appointment shall be booked")
		 public void the_appointment_shall_be_booked() {
		     assertTrue(currApp!=null);
		 }
		 
		 /**
		  * @author Shichang Zhang
		  * @author Junjian Chen
		  */
		 @Then("the service in the appointment shall be {string}")
		 public void the_service_in_the_appointment_shall_be(String string) {
		     assertEquals(string,currApp.getBookableService().getName());
		 }
		 
		 /**
		  * @author Shichang Zhang
		  */
		 @Then("the appointment shall be for the date {string} with start time {string} and end time {string}")
		 public void the_appointment_shall_be_for_the_date_with_start_time_and_end_time(String string, String string2, String string3) {
		     boolean flag=true;
		     Date dateCorrect = Date.valueOf(string);
		     String[] startTimeStringArray=string2.split(",");
		     String[] endTimeStringArray=string3.split(",");
		     List<Time> startTime=new ArrayList<Time>();
		     List<Time> endTime=new ArrayList<Time>();
		     for(int i=0;i<startTimeStringArray.length;i++) {
		    	 startTime.add(Time.valueOf(startTimeStringArray[i]+":00"));
		    	 endTime.add(Time.valueOf(endTimeStringArray[i]+":00"));
		     }
		     List<ServiceBooking> serviceBookings = currApp.getServiceBookings();
		     TimeSlot ts=null;
		     for(int i=0;i<serviceBookings.size();i++) {
		    	 ts=serviceBookings.get(i).getTimeSlot();
		    	 System.out.println(ts.getStartDate());
		    	 if(dateCorrect.compareTo(ts.getStartDate())!=0) {
		    		 flag=false;
		    	 }
		    	 if(startTime.get(i).compareTo(ts.getStartTime())!=0) {
		    		 flag=false;
		    	 }
		    	 if(endTime.get(i).compareTo(ts.getEndTime())!=0) {
		    		 flag=false;
		    	 }
		     }
		     
		     assertTrue(flag);
		 }
		 
		 /**
		  * @author Shichang Zhang
		  */
		 @Then("the username associated with the appointment shall be {string}")
		 public void the_username_associated_with_the_appointment_shall_be(String string) {
		     Customer customer = CarShopApplication.findCustomer(string);
		     assertEquals(customer,currApp.getCustomer());
		 }
		 
		 /**
		  * @author Shichang Zhang
		  */
		 @Then("the user {string} shall have {int} no-show records")
		 public void the_user_shall_have_no_show_records(String string, Integer int1) {
		     Customer customer = CarShopApplication.findCustomer(string);
			 assertEquals(int1.intValue(),customer.getTimeNotShow());
		 }
		 
		 /**
		  * @author Shichang Zhang
		  */
		 @Then("the system shall have {int} appointments")
		 public void the_system_shall_have_appointments(Integer int1) {
		     assertEquals(int1.intValue(),carShop.getAppointments().size());
		 }
		 
		 /**
		  * @author Junjian Chen
		  */
		 @When("{string} attempts to update the date to {string} and time to {string} at {string}")
		 public void attempts_to_update_the_date_to_and_time_to_at(String string, String string2, String string3, String appointmentDateAndTime) {
			 
			 try{
				 String curDate=appointmentDateAndTime.split("\\+")[0];
				 String curTime=appointmentDateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
				 CarShopController.updateDateAndTime(currApp, string, string2, string3);
			 }catch(RuntimeException e){
			    	error+=e.getMessage();
					errorCntr++;
			     }
		 }

		 /**
		  * @author Junjian Chen
		  */
		 @When("{string} attempts to cancel the appointment at {string}")
		 public void attempts_to_cancel_the_appointment_at(String string, String appointmentDateAndTime) {
			 
			 try {
				 String curDate=appointmentDateAndTime.split("\\+")[0];
				 String curTime=appointmentDateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));	
				 try {
					CarShopController.endAppointment(currApp, string);
				} catch (InvalidInputException e) {
					error+=e.getMessage();
					errorCntr++;
					return;
				}
			 }catch(RuntimeException e){
			    	error+=e.getMessage();
					errorCntr++;
			 }
		 }

		 /**
		  * @author John
		  */
		 @Then("the system shall have {int} appointment")
		 public void the_system_shall_have_appointment(Integer int1) {
		     assertEquals(int1.intValue(),carShop.getAppointments().size());
		 }
		 /**
		  * @author John
		 * @throws Exception 
		  */
		 @When("{string} makes a {string} appointment with service {string} for the date {string} and start time {string} at {string}")
		 public void makes_a_appointment_with_service_for_the_date_and_start_time_at(String customerName, String ComboService, String OptionalService, String date, String start, String DateAndTime) throws Exception {
			 
		     try {
		    	 pastAppointmentNum=carShop.getAppointments().size();
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
		    	 currApp = null;
		    	 currApp = CarShopController.makeComboAppointment(customerName, date, ComboService, OptionalService, start);
			} catch (InvalidInputException e) {
				error+=e.getMessage();
				errorCntr++;
			}
		 }

		 /**
		  * @author Yuyan Shi, Shichang Zhang
		  */
		 @When("{string} attempts to add the optional service {string} to the service combo with start time {string} in the appointment at {string}")
		 public void attempts_to_add_the_optional_service_to_the_service_combo_with_start_time_in_the_appointment_at(String user, String optionalService, String startTime, String appointmentDateAndTime) throws Exception {		 
		     try {
		    	 String curDate=appointmentDateAndTime.split("\\+")[0];
				 String curTime=appointmentDateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
		    	 String startTimeList="";
		    	 for(ServiceBooking sb : currApp.getServiceBookings()) {
		    		 String startTimeString = sb.getTimeSlot().getStartTime().toString();
		    		 startTimeList=startTimeList+","+startTimeString.substring(0, startTimeString.length()-3);
		    	 }
				 CarShopController.updateServiceComboOfAppointment(currApp, user, optionalService, startTime,currApp.getServiceBookings().get(0).getTimeSlot().getStartDate().toString(),startTimeList.substring(1));
		     } catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		 }

		 /**
		  * @author Yuyan Shi
		  */
		 @Then("the service combo in the appointment shall be {string}")
		 public void the_service_combo_in_the_appointment_shall_be(String string) {
			 assertEquals(currApp.getBookableService().getName(), string);
		 }

		 /**
		  * @author Yuyan Shi, John
		  * @author Junjian Chen, Shichang Zhang
		 * @throws Exception 
		  */
		 @Then("the service combo shall have {string} selected services")
		 public void the_service_combo_shall_have_selected_services(String string) throws Exception {			 
			 List<ServiceBooking> sbList = currApp.getServiceBookings();
			 String[] services=string.split(",");
			 boolean flag=true;
			 for(int i=0;i<sbList.size();i++) {
				 if(!services[i].equals(sbList.get(i).getService().getName())) {
					 flag=false;
				 }
			 }
			 assertTrue(flag);
			 }

		 /**
		  * @author Junjian Chen
		  */
		 @When("{string} attempts to update the date to {string} and start time to {string} at {string}")
		 public void attempts_to_update_the_date_to_and_start_time_to_at(String string, String string2, String string3, String DateAndTime) {
			 
		     try {
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
				 CarShopController.updateDateAndTime(currApp, string, string2, string3);
		     }catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		     
		 }

		 /**
		  * @author John
		  */
		 @When("the owner starts the appointment at {string}")
		 public void the_owner_starts_the_appointment_at(String DateAndTime) {
			 
		     try {
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
			     
			     try {
			    	 Owner owner = CarShopApplication.getCarShop().getOwner();
			    	CarShopController.logIn(owner.getUsername(), owner.getPassword());
					CarShopController.ownerStartApp(currApp);
				} catch (InvalidInputException e) {
					error += e.getMessage();
			    	 errorCntr++;
			    	 return;
				}
		     }catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		 }
		 
		 /**
		  * @author John
		  */
		 @When("the owner ends the appointment at {string}")
		 public void the_owner_ends_the_appointment_at(String DateAndTime) {
			 
		     try {
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
			     
			     try {
			    	Owner owner = CarShopApplication.getCarShop().getOwner();
					CarShopController.logIn(owner.getUsername(), owner.getPassword());
					CarShopController.ownerEndAppNormally(currApp);
				} catch (InvalidInputException e) {
					error += e.getMessage();
			    	errorCntr++;
			    	return;
				}
		     }catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		 }
		 /**
		  * @author John
		 * @throws Exception 
		  */
		 @Then("the appointment shall be in progress")
		 public void the_appointment_shall_be_in_progress() throws Exception {
		     assertTrue(currApp.getStatus().equals(Appointment.Status.StartApp));
		 }
		 /**
		  * @author John
		  */
		 @When("the owner attempts to register a no-show for the appointment at {string}")
		 public void the_owner_attempts_to_register_a_no_show_for_the_appointment_at(String DateAndTime) {
			 
		     try {
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
			     try {
			    	Owner owner = CarShopApplication.getCarShop().getOwner();
				    CarShopController.logIn(owner.getUsername(), owner.getPassword());
					CarShopController.notShowUp(currApp);
				} catch (InvalidInputException e) {
					error += e.getMessage();
			    	 errorCntr++;
			    	 return;
				} 
		     }catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		     
		 }
		 
		 /**
		  * @author John
		  */
		 @When("the owner attempts to end the appointment at {string}")
		 public void the_owner_attempts_to_end_the_appointment_at(String DateAndTime) {
			 
		     try {
		    	 String curDate=DateAndTime.split("\\+")[0];
				 String curTime=DateAndTime.split("\\+")[1];
			     carShop.setCurrentDate(Date.valueOf(curDate));
			     carShop.setCurrentTime(Time.valueOf(curTime+":00"));
			     try {
			    	Owner owner = CarShopApplication.getCarShop().getOwner();
					CarShopController.logIn(owner.getUsername(), owner.getPassword());
					CarShopController.ownerEndAppNormally(currApp);
				} catch (InvalidInputException e) {
					error += e.getMessage();
			    	errorCntr++;
				} 
		     }catch(RuntimeException e) {
		    	 error += e.getMessage();
		    	 errorCntr++;
		     }
		 }
		 
}