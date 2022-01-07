package ca.mcgill.ecse.carshop.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.model.*;
import ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.persistence.CarShopPersistence;

public class CarShopController {
	public CarShopController() {
		
	}
	
	//Yuyan Shi-Evan----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * this method takes in two String inputs as username and password
	 * it judges whether the condition for sign up is satisfied first and then create a new customer account
	 * invalid input, duplicate username and state of logged in as owner or technician will prevent the creating of new customer account
	 * @author Yuyan Shi
	 * 
	 */
	public static void signUp(String username, String password) throws InvalidInputException {
		CarShopApplication.setOldNumOfAccount(CarShopApplication.getNumOfAccount());
		CarShop cs = CarShopApplication.getCarShop();
		if(CarShopApplication.getCurrentUser()!=null) {
			if(CarShopApplication.getLoginState() && CarShopApplication.getCurrentUser().equals(cs.getOwner())) {
				throw new InvalidInputException("You must log out of the owner account before creating a customer account");
			}
			if(CarShopApplication.getLoginState() && (CarShopApplication.getCurrentUser() instanceof Technician)) {
				throw new InvalidInputException("You must log out of the technician account before creating a customer account");
			}
		}
		if(username.isBlank() && password.isBlank()) {
			throw new InvalidInputException("The username and password cannot be empty");
		}
		if(username.isBlank()) {
			throw new InvalidInputException("The user name cannot be empty");
		}
		if(password.isBlank()) {
			throw new InvalidInputException("The password cannot be empty");
		}
		List<Customer> customers = cs.getCustomers();
		for(int i = 0; i < customers.size(); i++) {
			if(username.equals(customers.get(i).getUsername())) {
				throw new InvalidInputException("The username already exists");
			}
		}
		
		try {
			cs.addCustomer(username, password);
			CarShopPersistence.save(cs);
		}
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
		
	}
	
	/**
	 * this method takes in two String inputs as username and password that the user would like to update
	 * it judges whether the condition for updating is satisfied first and then update the existing account
	 * invalid input, existing username and not logged in will prevent updating customer account
	 * changing username for owner and technician account will not be allowed
	 * @author Yuyan Shi
	 */
	public static void updateAccount(String newUsername, String newPassword) throws InvalidInputException {
		CarShopApplication.setAccountUpdatedState(false);
		CarShop cs = CarShopApplication.getCarShop();
		if(!CarShopApplication.getLoginState()) {
			throw new InvalidInputException("User should login to update their info");
		}
		if(newUsername.isBlank() && newPassword.isBlank()) {
			throw new InvalidInputException("The username and password cannot be empty");
		}
		if(newUsername.isBlank()) {
			throw new InvalidInputException("The user name cannot be empty");
		}
		if(newPassword.isBlank()) {
			throw new InvalidInputException("The password cannot be empty");
		}
		if(CarShopApplication.getCurrentUser().equals(cs.getOwner())) {
			if(!newUsername.equals(CarShopApplication.getCurrentUser().getUsername())) {
				throw new InvalidInputException("Changing username of owner is not allowed");
			}
			try {
				cs.getOwner().setPassword(newPassword);
				CarShopApplication.setAccountUpdatedState(true);
				CarShopPersistence.save(cs);
			}
			catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		} else if(CarShopApplication.getCurrentUser() instanceof Technician) {
			if(!newUsername.equals(CarShopApplication.getCurrentUser().getUsername())) {
				throw new InvalidInputException("Changing username of technician is not allowed");
			}
			List<Technician> technicians = cs.getTechnicians();
			for(int i = 0; i < technicians.size(); i++) {
				if(newUsername.contentEquals(technicians.get(i).getUsername())) {
					throw new InvalidInputException("Username not available");
				}
			}
			try {
				CarShopApplication.getCurrentUser().setPassword(newPassword);
				CarShopApplication.setAccountUpdatedState(true);
				CarShopPersistence.save(cs);
			}
			catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
/*			for(int i = 0; i < technicians.size(); i++) {
				if(CarShopApplication.getCurrentUser().getUsername().equals(technicians.get(i).getUsername())) {
					cs.getTechnician(i).setPassword(newPassword);
				}
			}
*/		} else {
			List<Customer> customers = cs.getCustomers();
			for(Customer c : customers) {
				if(c.equals(CarShopApplication.getCurrentUser())) continue;
				if(c.getUsername().equals(newUsername)) throw new InvalidInputException("Username not available");
			}
			
			try {
				CarShopApplication.getCurrentUser().setUsername(newUsername);
				CarShopApplication.getCurrentUser().setPassword(newPassword);
				CarShopApplication.setAccountUpdatedState(true);
				CarShopPersistence.save(cs);
			}
			catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
/*			for(int i = 0; i < customers.size(); i++) {
				if(CarShopApplication.getCurrentUser().getUsername().equals(customers.get(i).getUsername())) {
					cs.getCustomer(i).setUsername(newUsername);
					cs.getCustomer(i).setPassword(newPassword);
				}
			}
*/		}
	}
	
	//Yuyan Shi-Evan----------------------------------------------------------------------------------------------------------------------------------
	
	
	//John Wang----------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * check if the current user is the owner 
	 * @throws Exception exception raised if not the owner.
	 */
	private static void checkOwner(String exceptionMsg) throws InvalidInputException {
		User curUser=CarShopApplication.getCurrentUser();
        if (!curUser.getUsername().equals("owner")) {
			throw new InvalidInputException(exceptionMsg);
		}
    }

    /**
     * 
     * @param name  name of the new business.
     * @param address  address of the new business.
     * @param phoneNumber  phoneNumber of the new business.
     * @param email  email of the new business.
     * @throws Exception  exception that raised by wrong input params or during the process of adding the business.
     */
	public static void setupBusinessInformation(String name, String address, String phoneNumber, String email) throws Exception {
												// "<name>" and "<address>" and "<phone number>" and "<email>"
		checkOwner("No permission to set up business information");
		
		if (!(email.endsWith(".com") && email.contains("@") && !email.startsWith("@") && !email.contains("@.com"))) { // email needs to be in a form of xxx@xxx.com
			throw new InvalidInputException("Invalid email");
		}
		
		if (name.length()==0) { 
			throw new InvalidInputException("please enter a Car Shop name");
		}

		if (address.length()==0) { 
			throw new InvalidInputException("please enter a Address");
		}

		if (phoneNumber.length()==0) { 
			throw new InvalidInputException("please enter a Phone number");
		}
	
		Business business = new Business(name, address, phoneNumber, email, CarShopApplication.getCarShop());
		CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
		
	}
	/**
	 * This method works as a helper method for setup/updat BusinessHour
	 * @param dayString current Day
	 * @param startTime current startTime
	 * @param endTime	current endTime
	 * @throws InvalidInputException it is thrown when : 1.user other than owner want to access it 2.Start time before End 3. Overlapping with existing businessInfo
	 */
	private static void checkBusinessHour(String dayString, String startTime, String endTime) throws InvalidInputException{

		checkOwner("No permission to update business information"); 
		startTime += ":00";
		endTime += ":00";
		DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayString);
		Time start =  Time.valueOf(startTime); //valueOf is method from Time class which convert a String into Time 
		Time end = Time.valueOf(endTime);

				
		if (start.after(end)) {
			throw new InvalidInputException("Start time must be before end time");
		}

		//get all business hour
		for (BusinessHour businessHour : CarShopApplication.getCarShop().getHours()) {
			if (businessHour.getDayOfWeek() == day) {  // if it is on the same day and.....		

				// case 1: overlap
				//start-------------end    			existing business hour
				//          start----------end      the hour trying to add
				// case 2: overlap
				//        start-------------end
				// start---------end
				if (businessHour.getEndTime().after(start) || businessHour.getStartTime().before(end) ) { // if(case 1 || case 2), time are overlapped
					throw new InvalidInputException("The business hours cannot overlap");
				}	
			}
		}

		
	}

	/**
	 * 
	 * @param dayString	 date of the new business hour
	 * @param startTime  start time of the new business hour
	 * @param endTime  end time of the new business hour
	 * @throws Exception  exception that raised by wrong input params or during the process of adding the business hour
	 */
	public static void addBusinessHour(String dayString, String startTime, String endTime) throws InvalidInputException{
											// Day      | newStartTime | newEndTime
		CarShopApplication.setBusinessHourUpdateState(false);
		if (startTime.length()==0 || endTime.length()==0) {
			throw new InvalidInputException("Please enter botn Start time and EndTime");
		}
		if(CarShopApplication.getCarShop().getBusiness()==null) {
			throw new InvalidInputException("Fail to add, since there is no business");
		}
		checkBusinessHour(dayString, startTime, endTime); // checks for user type and BusinessHour constrains
		
		try {
			startTime += ":00";
			endTime += ":00";
			DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayString);
			Time start =  Time.valueOf(startTime); //valueOf is method from Time class which convert a String into Time 
			Time end = Time.valueOf(endTime);

			BusinessHour businesshour = new BusinessHour(day, start, end, CarShopApplication.getCarShop());

			CarShopApplication.getCarShop().getBusiness().addBusinessHour(businesshour);
			CarShopApplication.setBusinessHourUpdateState(true);
			CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
		} catch(Exception e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	
	/**
	 * 
	 * @param type holiday or vacation
	 * @param startDateString 
	 * @param startTimeString
	 * @param endDateString
	 * @param endTimeString
	 * @throws InvalidInputException 1. not owner 2. wrong formatted email 3. start before end 4.start in the past 5.overlap with existing Sys TimeSlot.
	 */
	public static void addTimeSlot(String type, String startDateString, String startTimeString, String endDateString, String endTimeString) throws InvalidInputException {
		checkOwner("No permission to update business information");
		Date startDate = Date.valueOf(startDateString);
		Date endDate = Date.valueOf(endDateString);

		Time startTime = Time.valueOf(startTimeString + ":00");
		Time endTime = Time.valueOf(endTimeString + ":00");
		CarShop carShop = CarShopApplication.getCarShop();
		TimeSlot timeSlot = new TimeSlot(startDate, startTime, endDate, endTime, carShop);

	
		
		if (CarShopApplication.checkTime1BeforeTime2(startDate, startTime, carShop.getCurrentDate(), CarShopApplication.getCarShop().getCurrentTime())) {
			if ("holiday".equals(type)) { // if user wants to add a holiday before the system time
				throw new InvalidInputException("Holiday cannot start in the past");
			} else { // if user wants to add a vacation before the system time
				throw new InvalidInputException("Vacation cannot start in the past");
			}
		}
		
		if (!CarShopApplication.checkTime1BeforeTime2(startDate, startTime, endDate, endTime)) { 
			throw new InvalidInputException("Start time must be before end time");
		}

		if ("holiday".equals(type)) { // if user wants to add a holiday
			if (CarShopApplication.checkTimeSlotListOverlapped(timeSlot, CarShopApplication.getCarShop().getBusiness().getHolidays())) {
				throw new InvalidInputException("Holiday times cannot overlap "); // holiday overlap with holiday
			}

			if (CarShopApplication.checkTimeSlotListOverlapped(timeSlot, CarShopApplication.getCarShop().getBusiness().getVacations())) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap"); // holiday overlapped with existing vacation
			}

			CarShopApplication.getCarShop().getBusiness().addHoliday(timeSlot);
			CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
		} else { // vise versa, if user wants to add a vacation
			if (CarShopApplication.checkTimeSlotListOverlapped(timeSlot, CarShopApplication.getCarShop().getBusiness().getVacations())) {
				throw new InvalidInputException("Vacation times cannot overlap ");
			}

			if (CarShopApplication.checkTimeSlotListOverlapped(timeSlot, CarShopApplication.getCarShop().getBusiness().getHolidays())) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap");
			};
			CarShopApplication.getCarShop().getBusiness().addVacation(timeSlot);
			CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
		}


	}

	/**
	 * 
	 * @param name Business name
	 * @param address
	 * @param phoneNumber
	 * @param email
	 * @throws InvalidInputException 1. not owner 2. wrong formatted email
	 */
	public static void updateBusinessInformation(String name, String address, String phoneNumber, String email) throws InvalidInputException {
		CarShopApplication.setBusinessHourUpdateState(false);
		checkOwner("No permission to update business information");
		
		if (!(email.endsWith(".com") && email.contains("@") && !email.startsWith("@") && !email.contains("@.com"))) { // email needs to be in a form of xxx@xxx.com
			throw new InvalidInputException("Invalid email");
		}
	 
		// if current user is the owner and email is formated correctly, 
		CarShopApplication.getCarShop().getBusiness().setAddress(address);
		CarShopApplication.getCarShop().getBusiness().setName(name);
		CarShopApplication.getCarShop().getBusiness().setPhoneNumber(phoneNumber);
		CarShopApplication.getCarShop().getBusiness().setEmail(email);
		CarShopApplication.setBusinessHourUpdateState(true);
		CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
	}
	
	/**
	 * 
	 * @param curDate  Date of BusinessHour wants to update
	 * @param curStartTimeString  start time of BusinessHour wants to update
	 * @param dayString  Target day
	 * @param startTimeString  Target Time
	 * @param endTimeString Target endTime
	 * @throws InvalidInputException  1.user other than owner want to access it 2.Start time before End 3. Overlapping with existing businessInfo
	 */
	public static void updateExistingBusinessHours(String curDate, String curStartTimeString, String dayString, String startTimeString, String endTimeString) throws InvalidInputException {
		CarShopApplication.setBusinessHourUpdateState(false);
		checkOwner("No permission to update business information"); 
		if (curStartTimeString.length() == 0 || startTimeString.length() == 0 || endTimeString.length() == 0) {
			throw new InvalidInputException("Please enter the following 3 field ---1.Exsisting Start time 2.To start time 3.ToEndTime");
		}
		startTimeString += ":00";
		endTimeString += ":00";
		
		BusinessHour.DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayString);
		Time start =  Time.valueOf(startTimeString); //valueOf is method from Time class which convert a String into Time 
		Time end = Time.valueOf(endTimeString);

				
		if (start.after(end)) {
			throw new InvalidInputException("Start time must be before end time");
		}

		//get all business hour
		for (BusinessHour businessHour : CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
			if (businessHour.getDayOfWeek() .equals (CarShopApplication.getDayOfWeekFromString(curDate))) {
				if(businessHour.getStartTime().compareTo(Time.valueOf(curStartTimeString+":00"))==0) 
				continue; // don't compare with the given business hour itself, so we continue
			}
				
			if (businessHour.getDayOfWeek() == day) {  // if it is on the same day and it is one of the two overlapping cases
				// case 1: overlap
				//start-------------end    			existing business hour
				//          start----------end      the hour trying to add
				// case 2: overlap
				//        start-------------end
				// start---------end
				if (businessHour.getEndTime().after(start) || businessHour.getStartTime().before(end) ) { // if(case 1 || case 2), time are overlapped
					throw new InvalidInputException("The business hours cannot overlap");
				}	
			}
		}
		try {
			curStartTimeString += ":00";
			//locate the businessHour and set it the Target Date/Time
			for (BusinessHour b: CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
				if (b.getDayOfWeek() == CarShopApplication.getDayOfWeekFromString(curDate)
				&& b.getStartTime().equals(Time.valueOf(curStartTimeString))) {
					b.setStartTime(start);
					b.setEndTime(end);
					b.setDayOfWeek(CarShopApplication.getDayOfWeekFromString(dayString));
					CarShopApplication.setBusinessHourUpdateState(true);
					CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
					break;
				}
			}
		} catch(Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param curDate
	 * @param curStartTimeString
	 * @throws InvalidInputException 1. not the owner
	 */
	public static void removeExistingBusinessHours(String curDate, String curStartTimeString) throws InvalidInputException{
		checkOwner("No permission to update business information"); 
		curStartTimeString += ":00";
		BusinessHour businessHourToDelete = null;
		//find the Hour
		for (BusinessHour b: CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
			if (b.getDayOfWeek() == CarShopApplication.getDayOfWeekFromString(curDate)
			&& b.getStartTime().equals(Time.valueOf(curStartTimeString))) {
				businessHourToDelete = b;
			}
		}
		// And delete it
		CarShopApplication.getCarShop().getBusiness().removeBusinessHour(businessHourToDelete);	
		CarShopPersistence.save(CarShopApplication.getCarShop());  //delete and save
	}
	
	/**
	 * 
	 * @param type vacation or holiday
	 * @param curStartDateString 
	 * @param curStartTimeString
	 * @param targetStartDateString
	 * @param targetStartTimeString
	 * @param targetEndDateString
	 * @param targetEndTimeString
	 * @throws InvalidInputException 1.user other than owner want to access it 2.Start time before End 3. Overlapping with existing businessInfo
	 */
	public static void updateVacation(String type, String curStartDateString, String curStartTimeString, String targetStartDateString, String targetStartTimeString, String targetEndDateString, String targetEndTimeString) throws InvalidInputException {
		checkOwner("No permission to update business information"); 

		Date curDay = Date.valueOf(curStartDateString);
		Time curStart =  Time.valueOf(curStartTimeString + ":00"); //valueOf is method from sql Time/Date class which convert a String into Time/Date 

		Date targetStartDay = Date.valueOf(targetStartDateString);
		Date targetEndDay = Date.valueOf(targetEndDateString);
		
		Time targetStart = Time.valueOf(targetStartTimeString + ":00");
		Time targetEnd = Time.valueOf(targetEndTimeString + ":00");
		TimeSlot curtTimeSlot = new TimeSlot(targetStartDay, targetStart, targetEndDay, targetEnd, CarShopApplication.getCarShop());

		if (!CarShopApplication.checkTime1BeforeTime2(targetStartDay, targetStart, targetEndDay, targetEnd)) {
			throw new InvalidInputException("Start time must be before end time");
		}

		if (CarShopApplication.checkTime1BeforeTime2(targetStartDay, targetStart, CarShopApplication.getCarShop().getCurrentDate(), CarShopApplication.getCarShop().getCurrentTime())) {
			throw new InvalidInputException("Vacation cannot start in the past");
		}


		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getHolidays()) {
			if (CarShopApplication.checkTimeSlotOverlapped(curtTimeSlot, v)) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap");
			}
		}

		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getVacations()) {
			if (v.getStartDate().equals(curDay) && v.getStartTime().equals(curStart)) {
				continue;
			}
			if (CarShopApplication.checkTimeSlotOverlapped(curtTimeSlot, v)) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap");
			}
		}

		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getVacations()) {
			if (v.getStartDate().equals(curDay) && v.getStartTime().equals(curStart)) {
				v.setStartDate(targetStartDay);
				v.setEndDate(targetEndDay);

				v.setStartTime(targetStart);
				v.setEndTime(targetEnd);
				CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
			}
		}

	
	}

	/**
	 * 
	 * @param type vacation or holiday
	 * @param curStartDateString
	 * @param curStartTimeString
	 * @param targetStartDateString
	 * @param targetStartTimeString
	 * @param targetEndDateString
	 * @param targetEndTimeString
	 * @throws InvalidInputException 1.user other than owner want to access it 2.Start time before End 3. Overlapping with existing businessInfo
	 */
	public static void updateHoliday(String type, String curStartDateString, String curStartTimeString, String targetStartDateString, String targetStartTimeString, String targetEndDateString, String targetEndTimeString) throws InvalidInputException {
		checkOwner("No permission to update business information"); 

		Date curDay = Date.valueOf(curStartDateString);
		Time curStart =  Time.valueOf(curStartTimeString+ ":00"); //valueOf is method from Time class which convert a String into Time 

		Date targetStartDay = Date.valueOf(targetStartDateString);
		Date targetEndDay = Date.valueOf(targetEndDateString);
		
		Time targetStart = Time.valueOf(targetStartTimeString+ ":00");
		Time targetEnd = Time.valueOf(targetEndTimeString+ ":00");
		TimeSlot curtTimeSlot = new TimeSlot(targetStartDay, targetStart, targetEndDay, targetEnd, CarShopApplication.getCarShop());


		if (!CarShopApplication.checkTime1BeforeTime2(targetStartDay, targetStart, targetEndDay, targetEnd)) {
			throw new InvalidInputException("Start time must be before end time");
		}

		if (CarShopApplication.checkTime1BeforeTime2(targetStartDay, targetStart, CarShopApplication.getCarShop().getCurrentDate(), CarShopApplication.getCarShop().getCurrentTime())) {
			throw new InvalidInputException("Holiday cannot start in the past");
		}


		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getHolidays()) {
			if (v.getStartDate().equals(curDay) && v.getStartTime().equals(curStart)) {
				continue;
			}
			if (CarShopApplication.checkTimeSlotOverlapped(curtTimeSlot, v)) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap");
			}
		}

		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getVacations()) {
			if (CarShopApplication.checkTimeSlotOverlapped(curtTimeSlot, v)) {
				throw new InvalidInputException("Holiday and vacation times cannot overlap");
			}
		}

		for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getHolidays()) {
			if (v.getStartDate().equals(curDay) && v.getStartTime().equals(curStart)) {
				v.setStartDate(targetStartDay);
				v.setEndDate(targetEndDay);

				v.setStartTime(targetStart);
				v.setEndTime(targetEnd);
				CarShopPersistence.save(CarShopApplication.getCarShop());  //set and save
			}
		}

	
	}
	
	/**
	 * 
	 * @param type holiday or vacation
	 * @param startDateString
	 * @param startTimeString
	 * @param endDateString
	 * @param endTimeString
	 * @throws InvalidInputException 1.user other than owner want to access it
	 */
	public static void removeExistingTimeSlot(String type, String startDateString, String startTimeString, String endDateString, String endTimeString) throws InvalidInputException{
		checkOwner("No permission to update business information"); 


		Date targetStartDay = Date.valueOf(startDateString);
		Date targetEndDay = Date.valueOf(endDateString);
		
		Time targetStart = Time.valueOf(startTimeString+ ":00");
		Time targetEnd = Time.valueOf(endTimeString+ ":00");

		TimeSlot timeSlot = null;
		//if wants to remove vacation locate it in the list of TimeSlot of Business
		if (type.equals("vacation")) {
			for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getVacations()) {
				if (v.getStartDate().equals(targetStartDay) && v.getStartTime().equals(targetStart) 
				&& v.getEndDate().equals(targetEndDay) && v.getEndTime().equals(targetEnd) ) {
					timeSlot = v;
					break;
				}
			}
			// then remove it
			CarShopApplication.getCarShop().getBusiness().removeVacation(timeSlot);
		}
		// vice versa
		if (type.equals("holiday")) {
			for (TimeSlot v: CarShopApplication.getCarShop().getBusiness().getHolidays()) {
				if (v.getStartDate().equals(targetStartDay) && v.getStartTime().equals(targetStart) 
				&& v.getEndDate().equals(targetEndDay) && v.getEndTime().equals(targetEnd) ) {
					timeSlot = v;
					break;
				}
			}
			CarShopApplication.getCarShop().getBusiness().removeHoliday(timeSlot);
			CarShopPersistence.save(CarShopApplication.getCarShop());  //remove and save
		}

	}

	/**
	 * 
	 * @param user given a user logged in
	 * @return a Transfer Object Business that only contains name, address, phone number, and email of the business.
	 */
	public static TOBusiness ViewBusinessInfo(User user) {
		CarShop cs=CarShopApplication.getCarShop();
		Business bs = cs.getBusiness();
		TOBusiness tOBS = new TOBusiness(bs.getName(), bs.getAddress(), bs.getPhoneNumber(), bs.getEmail());
		CarShopPersistence.save(CarShopApplication.getCarShop());  //create and save
		return tOBS;
	}

	//John Wang----------------------------------------------------------------------------------------------------------------------------------
	
	
	//Junjian Chen----------------------------------------------------------------------------------------------------------------------------------
		public static void createCombo (String username, String comboName, String mainService, String services, String mandatory) throws Exception{
			//Split the String to get a list-----------------------------------------------------
			String[] serviceList=services.split(",");
			String[] mandatoryList=mandatory.split(",");
			//Initialize Input Variable for createCombo()------------------------------------------------
			Service inputMainService=null;
			List<Service> inputServices=new ArrayList<Service>();
			List<Boolean> inputMandatory=new ArrayList<Boolean>();
			//Compute Input Variable-----------------------------------------------------
			inputMainService=CarShopApplication.findService(mainService);
			String error="";
			for(int i=0;i<serviceList.length;i++) {
				Service addedService=CarShopApplication.findService(serviceList[i]);
				
				if(addedService==null) {
					error += "Service "+serviceList[i]+" does not exist!";
					break;
				}
				inputServices.add(addedService);
			}
			for(int i=0;i<mandatoryList.length;i++) {
				inputMandatory.add(mandatoryList[i].equals("true"));
			}
			
			//-------2.Main Service must be included error
			if(services.contains(mainService)==false) {
				error+="Main service must be included in the services!";
			}
			//-------3.Main Service Must be Mandatory Error
			if(inputServices.indexOf(inputMainService)!=-1) {
				if(inputMandatory.get(inputServices.indexOf(inputMainService))==false) {
				error+=" Main service must be mandatory!";
				}
			}
			
			//-------4.At least two service error
			if(inputServices.size()<2) {
				error+="A service Combo must contain at least 2 services!";
			}
			//-------6.Already Exist Error
			if (CarShopApplication.findCombo(comboName) != null) {
				error+= "Service combo " + comboName + " already exists";
			}	
			//-------7.Unauthorized Error
			if (!username.equals("owner")) {
				error += "You are not authorized to perform this operation";
			}
			//-------If having error, throw it			
			if (error.length() > 0) {
				throw new Exception(error.trim());
			}
			//-------Define New Combo Operation
			try {
				ServiceCombo serviceCombo = new ServiceCombo(comboName,CarShopApplication.getCarShop());
				for (int i = 0; i < inputServices.size(); i++) {
					serviceCombo.addService(inputMandatory.get(i), inputServices.get(i));
				}
				List<ComboItem> itemList=serviceCombo.getServices();
				for(ComboItem i:itemList) {
					if(i.getService().getName().equals(mainService)) {
						serviceCombo.setMainService(i);
						break;
					}
				}
				CarShopPersistence.save(CarShopApplication.getCarShop());
			} catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
		
		public static void updatingCombo(String username,String oldName, String newName,String mainService, String services,String mandatory) throws Exception{
			String error="";
			String[] serviceList=services.split(",");
			String[] mandatoryList=mandatory.split(",");
			//Initialize Input Variable for createCombo()------------------------------------------------
			Service inputMainService=null;
			List<Service> inputServices=new ArrayList<Service>();
			List<Boolean> inputMandatory=new ArrayList<Boolean>();
			//Compute Input Variable-----------------------------------------------------
			inputMainService=CarShopApplication.findService(mainService);
			for(int i=0;i<mandatoryList.length;i++) {
				inputMandatory.add(mandatoryList[i].equals("true"));
			}
			//-------1/5.Does not exist error
			for(int i=0;i<serviceList.length;i++) {
				Service addedService=CarShopApplication.findService(serviceList[i]);
				if(addedService==null) {
					error += "Service "+serviceList[i]+" does not exist!";
				}
			inputServices.add(addedService);
			}
			//-------2.Main Service must be included error
			if(services.contains(mainService)==false) {
				error+="Main service must be included in the services!";
			}
			//-------3.Main Service Must be Mandatory Error
			if(services.indexOf(mainService)!=-1) {
				if(inputMandatory.get(inputServices.indexOf(inputMainService))==false) {
				error+=" Main service must be mandatory!";
				}
			}
			
			//-------4.At least two service error
			if(inputServices.size()<2) {
				error+="A service Combo must contain at least 2 services!";
			}
			//-------6.Already Exist Error
			if(!newName.equals(oldName)) {//in this case we want to update the current combo, so don't throw error
				if (CarShopApplication.findCombo(newName) != null) {
				error+= "Service "+newName+" already exists";
				}	
			}
			//-------7.Unauthorized Error
			if (!username.equals("owner")) {
				error+= "You are not authorized to perform this operation";
			}
			//-------If having error, throw it			
			if (error.length() > 0) {
				throw new Exception(error.trim());
			}
			//-------update Combo Operation
			try {
				ServiceCombo oldCombo = CarShopApplication.findCombo(oldName);
				CarShopApplication.getCarShop().removeBookableService(oldCombo);
				oldCombo.delete();
				ServiceCombo newCombo=new ServiceCombo(newName,CarShopApplication.getCarShop());
				for(int i=0;i<inputMandatory.size();i++) {
					newCombo.addService(inputMandatory.get(i), inputServices.get(i));
				}
				List<ComboItem> itemList=newCombo.getServices();
				for(ComboItem i:itemList) {
					if(i.getService().getName().equals(mainService)) {
						newCombo.setMainService(i);
						break;
					}
				}
				CarShopApplication.getCarShop().addBookableService(newCombo);
				CarShopPersistence.save(CarShopApplication.getCarShop());
			} catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
			
		}
	//Junjian Chen----------------------------------------------------------------------------------------------------------------------------------
	
	//Shichang Zhang--------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * add a service to the carshop system. The new service will have the name as input name
	 * and have duration as input duration and it will be done at garage indicated by
	 * the input garage name. 
	 * Only owner can add the service to the carShop system. If the service is already exist, it will not be added again.
	 * If the input information about the service is invalid, the method will throw InvalidInputException.
	 * 
	 * @param name service name
	 * @param duration service duration, must be positive
	 * @param garageName garage name
	 * @throws InvalidInputException input information is invalid
	 */
	public static void addService(String name, int duration, String garageName) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		String error = "";
		//check whether parameter duration is invalid
		if(duration<=0) {
			error = "Duration must be positive";
		}
		if(CarShopApplication.getCurrentUser()==null) throw new InvalidInputException("No user login");
		if(!CarShopApplication.getCurrentUser().equals(carShop.getOwner())) {
			error = "You are not authorized to perform this operation";
		}
		if(error.length()>0) {
			throw new InvalidInputException(error.trim());
		}
		try {
			Garage garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(garageName));
			boolean wasAdded=true;
			if(CarShopApplication.findBookableService(name)==null) {
				Service service = new Service(name,carShop,duration,garage);
			}else {
				wasAdded = false;
			}
			//check whether the service has already exist
			if (!wasAdded) {
				throw new InvalidInputException("Service wash already exists");
			}
			CarShopPersistence.save(carShop);
		}catch(RuntimeException e){
			String err = e.getMessage();
			if (e.getMessage().equals("Cannot create due to duplicate name. See http://manual.umple.org?RE003ViolationofUniqueness.html")) {
				err="Service wash already exists";
			}
			throw new InvalidInputException(err);
		}
	}
		
		/**
		 * update the service according to input information. The service will be updated
		 * to have the name as input name and have duration as input duration and will 
		 * be done at the garage indicated by the input garage name. 
		 * Only owner can update the service in the carShop system.
		 * If input is invalid, the method will throw InvalidInputException.
		 * 
		 * @param serviceName the name of the service to be updated
		 * @param name want the service's name to be updated with this
		 * @param duration want the service's duration to be updated with this, must be positive
		 * @param garage want the service's garage to be updated with the garage indicated by this name
		 * @throws InvalidInputException input information is invalid
		 * @author Shichang Zhang
		 */
		public static void updateService (String serviceName, String name, int duration, String garage) throws InvalidInputException {
			CarShop carShop = CarShopApplication.getCarShop();
	    	System.out.println(CarShopApplication.getCurrentUser().getUsername());
	    	//check the parameter duration 
			if(duration<=0) {
				throw new InvalidInputException("Duration must be positive");
			}
			//check whether the use login
			if(CarShopApplication.getCurrentUser()==null) throw new InvalidInputException("No user login");
			//check the authority
	    	if(!CarShopApplication.getCurrentUser().equals(carShop.getOwner())) {
				throw new InvalidInputException("You are not authorized to perform this operation");
			}
			Service service = (Service)CarShopApplication.findServiceWithName(serviceName);
			String typeOfGarageFromName = getTypeOfGarageFromName(name);
			//check the parameter name
			//check whether name is the proper format
			if(CarShopApplication.checkType(typeOfGarageFromName)) {
				//if service has different name with the new input name,should check whether need to update service name
				if(!serviceName.equals(name)) {
					//check whether system already has the service with input name
					if(CarShopApplication.findServiceWithName(name)!=null) {
						//the input name service has already exist
						throw new InvalidInputException("Service "+ name +" already exists");
					}else {
						//correct name, need to update to the name
						try {
							service.setName(name);
							CarShopPersistence.save(carShop);
						}catch(RuntimeException e) {
							throw new InvalidInputException(e.getMessage());
						}
					}
				}
			}else {
				throw new InvalidInputException("There is no " +typeOfGarageFromName+ " garage");
			}
			try {
				service.setDuration(duration);
				service.setGarage(CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(garage)));
				CarShopPersistence.save(carShop);
			}catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
				
		
		
		
		/**
		 * get the type of garage in String where service will be processed. 
		 * @param name service name
		 * @return the type of garage in String.
		 * @author Shichang Zhang
		 */
		private static String getTypeOfGarageFromName (String name) {
			//get type from the name
			String temp = name.split("-")[0];
			//turn the first char of the type to upper case
			String type = temp.substring(0, 1).toUpperCase()+temp.substring(1);
			return type;
		}
		//Shichang Zhang------------------------------------------------------------------------------------------------------
		
		
		//temp------------------------------------------
		
		/**
		 * let the user login. If The input information is invalid, throw InvalidInputException
		 * @param username user login with the username
		 * @param password uer login with the password
		 * @throws InvalidInputException input information is invalid
		 * @author Shichang Zhang
		 */
		public static void logIn(String username, String password) throws InvalidInputException{
			CarShopApplication.setLoginState(false);
			if(username==null || password == null) throw new InvalidInputException("Username/password not found");
			CarShop carShop = CarShopApplication.getCarShop();
			User user = CarShopApplication.findUserFromUsername(username);
			String [] words = username.split("-");
			
			//check whether the user account exist
			if(user == null) {
				//the user account does not exist
				if(username.equals("owner") && password.equals(username)) {
					//the account is the owner, the first time login
					try {
						CarShopApplication.setOldNumOfAccount(CarShopApplication.getNumOfAccount());
						user=new Owner("owner","owner",CarShopApplication.getCarShop());
						CarShopPersistence.save(carShop);
					}catch(RuntimeException e) {
						throw new InvalidInputException(e.getMessage());
					}
				}else if (words.length == 2 && CarShopApplication.checkType(words[0]) && words[1].equals("Technician") && password.equals(username)) {
					//the account is the Technician, the first time login
					try {
						CarShopApplication.setOldNumOfAccount(CarShopApplication.getNumOfAccount());
						TechnicianType type = CarShopApplication.getTechnicianTypeFromString(username.split("-")[0]);
						user = carShop.addTechnician(username, password, type);
						Garage garage = carShop.addGarage((Technician)user);
						if(carShop.getBusiness()!=null) {
							for(BusinessHour businessHour : carShop.getBusiness().getBusinessHours()) {
								garage.addBusinessHour(businessHour);
							}
						}
						CarShopPersistence.save(carShop);
					}catch(RuntimeException e) {
						throw new InvalidInputException(e.getMessage());
					}
				}else {
					//it is a consumer account, the account does not exist
					throw new InvalidInputException("Username/password not found");
				}
			}
			
			//the user account exists
			//check whether user login with correct password
			if(!user.getPassword().equals(password)) {
				//password is invalid
				throw new InvalidInputException("Username/password not found");
			}
				
			//login successfully
			CarShopApplication.setCurrentUser(user);
		}
		
		/**
		 * Logout the current user
		 */
		public static void logOut() throws RuntimeException{
			if(CarShopApplication.getLoginState()==true) {
				CarShopApplication.setCurrentUser(null);
			}else {
				throw new RuntimeException("No user login");
			}
			
		}
		
		
		/**
		 * add garage opening hour according to the input information, invalid input information will throw InvalidInputException
		 * @param username user login with the username
		 * @param dayString day of the opening hour
		 * @param startTimeString start time of the opening hour
		 * @param endTimeString end time of the opening hour
		 * @param typeString technician type of technician owning the garage
		 * @throws InvalidInputException invalid input parameters
		 * @author Shichang Zhang
		 */
		public static void addGarageOpeningHour(String username, String dayString, String startTimeString, String endTimeString, String typeString) throws InvalidInputException{
			CarShop carShop = CarShopApplication.getCarShop();
			
			User user;
			DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayString);
			Time startTime = Time.valueOf(startTimeString+":00");
			Time endTime = Time.valueOf(endTimeString+":00");
			Garage garage;
			
			//check whether the start time and end time are correct
			if(startTime.after(endTime)) {
				throw new InvalidInputException("Start time must be before end time");
			}
			
			//check whether the added opening hour is within the business hour
			if(!isInBusinessHours(carShop,startTime,endTime,day)) {
				throw new InvalidInputException("The opening hours are not within the opening hours of the business");
			}
			
			garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(typeString));
			//check overlap
			for(BusinessHour businessHour : garage.getBusinessHours()) {
				if(businessHour.getDayOfWeek().equals(day)){
					//check whether the start time is within the existing opening hour
					boolean startTimeOverlap =(startTime.compareTo(businessHour.getStartTime())>=0) && (startTime.compareTo(businessHour.getEndTime())<0);
					//check whether the end time is within the existing opening hour
					boolean endTimeOverlap = (endTime.compareTo(businessHour.getStartTime())>=0) && (endTime.compareTo(businessHour.getEndTime())<0); 
					if(startTimeOverlap || endTimeOverlap) {
						throw new InvalidInputException("The opening hours cannot overlap");
					}
				}
			}
			
			user = CarShopApplication.findUserFromUsername(username);
			//check whether user is a technician
			if(user instanceof Technician) {
				Technician technician = (Technician) user;
				//check whether the user is the correct technician owning the garage
				if(technician.getGarage().equals(garage)) {
					try {
						BusinessHour businessHour = new BusinessHour(day,startTime, endTime, carShop);
						garage.addBusinessHour(businessHour);
						CarShopPersistence.save(carShop);
					}catch(RuntimeException e) {
						throw new InvalidInputException(e.getMessage());
					}
					return;
				}
			}
			//the user is the owner or other technicians or consumers
			throw new InvalidInputException("You are not authorized to perform this operation");
		}
		
		/**
		 * remove garage opening hour according to the input information, invalid input information will throw InvalidInputException
		 * @param username user login with the username
		 * @param dayString day of the opening hour
		 * @param startTimeString start time of the opening hour
		 * @param endTimeString end time of the opening hour
		 * @param typeString technician type of technician owning the garage
		 * @throws InvalidInputException invalid input parameters
		 */
		public static void removeGarageOpeningHour(String username, String dayString, String startTimeString, String endTimeString, String typeString) throws InvalidInputException{
			CarShop carShop = CarShopApplication.getCarShop();
			
			User user;
			DayOfWeek day = CarShopApplication.getDayOfWeekFromString(dayString);
			Time startTime = Time.valueOf(startTimeString+":00");
			Time endTime = Time.valueOf(endTimeString+":00");
			Garage garage;
			
			//check whether the start time and end time are correct
			if(startTime.after(endTime)) {
				throw new InvalidInputException("Start time must be before end time");
			}
			
			//check whether the added opening hour is within the business hour
			if(!isInBusinessHours(carShop,startTime,endTime,day)) {
				throw new InvalidInputException("The opening hours are not within the opening hours of the business");
			}
			
			garage = CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(typeString));
			user = CarShopApplication.findUserFromUsername(username);
			//check whether user is a technician
			if(user instanceof Technician) {
				Technician technician = (Technician) user;
				//check whether the user is the correct technician owning the garage
				if(technician.getGarage().equals(garage)) {
					//check whether the car shop has the opening hour
					for(BusinessHour businessHour : garage.getBusinessHours()) {
						if(businessHour.getDayOfWeek().equals(day) && businessHour.getStartTime().equals(startTime) && businessHour.getEndTime().equals(endTime)) {
							try {
								garage.removeBusinessHour(businessHour);
								CarShopPersistence.save(carShop);
							}catch(RuntimeException e) {
								throw new InvalidInputException(e.getMessage());
							}
							return;
						}
					}
					return;
				}
			}
			//the user is the owner or other technicians or consumers
			throw new InvalidInputException("You are not authorized to perform this operation");
		}
		
		
		/**
		 * check whether the input information of the opening hour is within the business hour
		 * @param carShop car shop
		 * @param startTime start time of the opening hour
		 * @param endTime end time of the opening hour
		 * @param day day of the opening hour
		 * @return whether the opening hour is within the business hour of the car shop
		 */
		private static boolean isInBusinessHours(CarShop carShop, Time startTime, Time endTime,DayOfWeek day ) {
			boolean result = false;
			for(BusinessHour businessHour : carShop.getBusiness().getBusinessHours()) {
				if(businessHour.getDayOfWeek().equals(day)) {
					if((startTime.compareTo(businessHour.getStartTime())>=0) && (endTime.compareTo(businessHour.getEndTime())<=0) ) {
						result = true;
					}
				}
			}
			return result;
		}
		
		
		//Feature 6 Controller:Make/Cancel Appointment
				/**
				 * Add a service to a existing appointment
				 * @author Junjian Chen
				 * @param a existing Appointment
				 * @param myCustomer the appointment's customer
				 * @param myDate the appointment's date
				 * @param myService the service want to add in a service booking
				 * @param startTime the service booking's startTime
				 * @param endTime the service booking's endTime
				 * @throws InvalidInputException
				 */
				public static Appointment makeAppointment(String customer,String date, String serviceName, String startTimeString) throws InvalidInputException {
					Customer myCustomer = CarShopApplication.findCustomer(customer);
					Date myDate=Date.valueOf(date);
					Service myService=CarShopApplication.findService(serviceName);
					Time startTime=Time.valueOf(startTimeString+":00");
					Time endTime=CarShopApplication.afterTime(startTimeString, myService.getDuration());
					
					String error="";
					
					//test the user role
					if(myCustomer==null) {
						throw new InvalidInputException("Only customers can make an appointment");
					}
			
					
					//test whether the time slot is occupied by other service booking
					List<ServiceBooking> existingBookingOnThatDate=CarShopApplication.findServiceBookingtByDate(myDate);
					for(ServiceBooking s:existingBookingOnThatDate) {
						if(s.getService().getGarage().equals(myService.getGarage())) {
							boolean startOverlap = startTime.after(s.getTimeSlot().getStartTime()) && startTime.before(s.getTimeSlot().getEndTime());
							boolean endOverlap = endTime.after(s.getTimeSlot().getStartTime()) && endTime.before(s.getTimeSlot().getEndTime());
							if(startOverlap || endOverlap) {
								error+="There are no available slots for "+serviceName+" on "+myDate+" at "+startTimeString;
							}
							
						}
					}
					
					//test whether the time slot is in the business hour
					for(TimeSlot ts:CarShopApplication.getCarShop().getBusiness().getHolidays()) {
						if(myDate.compareTo(ts.getStartDate())>=0 && myDate.compareTo(ts.getEndDate())<=0) {
							error+="There are no available slots for "+serviceName+" on "+myDate+" at "+startTimeString;
						}
					}
					
					//check whether is in the garage opening hour
					Garage g=myService.getGarage();
					BusinessHour bh=null;
					for(BusinessHour b:g.getBusinessHours()) {
						if(b.getDayOfWeek().equals(CarShopApplication.getWeek(myDate))) {
							bh=b;
						}
					}
					if(bh==null) {
						error+="There are no available slots for "+serviceName+" on "+myDate+" at "+startTimeString;
					}else {
						if(startTime.before(bh.getStartTime())||endTime.after(bh.getEndTime())) {
							error+="There are no available slots for "+serviceName+" on "+myDate+" at "+startTimeString;
						}
					}
					
	/*				//4
					bh=null;
					for(BusinessHour b:CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
						if(b.getDayOfWeek().equals(CarShopApplication.getWeek(myDate))) {
							bh=b;
						}
					}
					if(bh==null) {
						error+="There are no available slots for "+myService.getName()+" on "+myDate+" at "+startTime;
					}else {
						if(startTime.before(bh.getStartTime())||endTime.after(bh.getEndTime())) {
							error+="There are no available slots for "+myService.getName()+" on "+myDate+" at "+startTime;
						}
					}
	*/				
					
					//check whether the date was in the past
					if(myDate.before(CarShopApplication.getCarShop().getCurrentDate())) {
						error+="There are no available slots for "+serviceName+" on "+myDate+" at "+startTimeString;
					}
					
					
					if (error.length() > 0) {
						throw new InvalidInputException(error.trim());
					}
					
					//for the BookableService is a Service
					try {
						Appointment a=CarShopApplication.getCarShop().addAppointment(myCustomer, myService);
						TimeSlot myTimeSlot=new TimeSlot(myDate, startTime, myDate, endTime, CarShopApplication.getCarShop());
						a.addServiceBooking(myService, myTimeSlot);
						a.setBookableService(myService);
						a.make();
						CarShopPersistence.save(CarShopApplication.getCarShop());
						return a;
					}catch(RuntimeException e) {
						throw new InvalidInputException(e.getMessage());
					}
				}
				
				
				/**
				 * A customer attempt to make a appointment of service combo
				 * @author Junjian Chen
				 * @param myCustomer
				 * @param myDate
				 * @param myCombo
				 * @param optionalService
				 * @param startTimeList
				 * @param endTimeList
				 * @throws InvalidInputException
				 */
				public static Appointment makeComboAppointment(String customer,String date, String myComboString,String optionalServiceString, String startTimes) throws InvalidInputException {
					String error="";
					Customer myCustomer = CarShopApplication.findCustomer(customer);
					Date myDate=Date.valueOf(date);
					ServiceCombo myCombo=CarShopApplication.findCombo(myComboString);
					String[] optionalServiceStringArr=optionalServiceString.split(",");
					List<Service> optionalService=new ArrayList<Service>();
					for(int j=0;j<optionalServiceStringArr.length;j++) {
						optionalService.add(CarShopApplication.findService(optionalServiceStringArr[j]));
					}
					List<Time> startTimeList=new ArrayList<Time>();
					List<Time> endTimeList=new ArrayList<Time>();
					String[] startTimeString=startTimes.split(",");
					for(int i=0;i<startTimeString.length;i++) {
						startTimeList.add(Time.valueOf(startTimeString[i]+":00"));
					}
					endTimeList.add(CarShopApplication.afterTime(startTimeString[0], myCombo.getMainService().getService().getDuration()));
					for(int j=0;j<optionalServiceStringArr.length;j++) {
						endTimeList.add(CarShopApplication.afterTime(startTimeString[j+1], optionalService.get(j).getDuration()));
					}
					
					List<Service> AllService=new ArrayList<Service>();
					AllService.add(myCombo.getMainService().getService());
					for(Service s:optionalService) {
						AllService.add(s);
					}
					
					//check user role
					if(myCustomer==null) {
						throw new InvalidInputException("Only customers can make an appointment");
					}
					//customer must have a time not show record less than 5
					if(myCustomer.getTimeNotShow()>=5) {
						throw new InvalidInputException("The customer "+myCustomer.getUsername()+" has "+myCustomer.getTimeNotShow()+" time not show records!");
					}
					//check whether the time slot is occupied by other appointments
					for(int i=0;i<startTimeList.size();i++) {
						
						Time myStartTime=startTimeList.get(i);
						Time myEndTime=endTimeList.get(i);
						List<ServiceBooking> existingBookingOnThatDate=CarShopApplication.findServiceBookingtByDate(myDate);
						for(ServiceBooking s:existingBookingOnThatDate) {
							if(s.getService().getGarage().equals(AllService.get(i).getGarage())) {
								boolean startOverlap = (myStartTime.compareTo(s.getTimeSlot().getStartTime())>0) && (myStartTime.compareTo(s.getTimeSlot().getEndTime())<=0);
								boolean endOverlap =(myEndTime.compareTo(s.getTimeSlot().getStartTime())>0) && (myEndTime.compareTo(s.getTimeSlot().getEndTime())<=0);
								if(startOverlap || endOverlap) {
									error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];
								}
							}
						}
						
						//2
						if(i!=startTimeList.size()-1) {
						if(myEndTime.after(startTimeList.get(i+1))==true){
							error+="Time slots for two services are overlapping";
						}
						//throw new Exception(myEndTime+"");
					}
					
					//3
					for(TimeSlot ts:CarShopApplication.getCarShop().getBusiness().getHolidays()) {
						if(myDate.after(ts.getStartDate())&&myDate.before(ts.getEndDate())) {
							error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];
						}
					}
					//4
					Garage g=CarShopApplication.findCombo(myCombo.getName()).getMainService().getService().getGarage();
					BusinessHour bh=null;
					for(BusinessHour b:g.getBusinessHours()) {
						if(b.getDayOfWeek().equals(CarShopApplication.getWeek(myDate))) {
							bh=b;
						}
					}
					if(bh==null) {
						error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];

					}else {
						if(myStartTime.before(bh.getStartTime())||myEndTime.after(bh.getEndTime())) {
							error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];

						}
					}
					//5
					bh=null;
					for(BusinessHour b:CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
						if(b.getDayOfWeek().equals(CarShopApplication.getWeek(myDate))) {
							bh=b;
						}
					}
					
					if(bh==null) {
						error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];
					}else {
						if(myStartTime.before(bh.getStartTime())||myEndTime.after(bh.getEndTime())) {
							error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];
						
						}
					}
					//6
					if(myDate.before(CarShopApplication.getCarShop().getCurrentDate())) {
						error+="There are no available slots for "+myComboString+" on "+date+" at "+startTimeString[i];
					}
					}

					if (error.length() > 0) {
						throw new InvalidInputException(error.trim());
					}
					try {
						Appointment a=new Appointment(myCustomer, myCombo, CarShopApplication.getCarShop());
						a.setBookableService(myCombo);
						a.addServiceBooking(myCombo.getMainService().getService(), new TimeSlot(myDate, startTimeList.get(0), myDate, endTimeList.get(0), CarShopApplication.getCarShop()));
						for(int j=0;j<optionalService.size();j++) {
							a.addServiceBooking(optionalService.get(j), new TimeSlot(myDate, startTimeList.get(j+1), myDate, endTimeList.get(j+1), CarShopApplication.getCarShop()));
						}
						a.make();
						CarShopPersistence.save(CarShopApplication.getCarShop());
						return a;
					}catch(RuntimeException e) {
						throw new InvalidInputException(e.getMessage());
					}
					
				}
				
				/**
				 * A User log in and attempt to cancel appointment on a date for a service at a time.
				 * @param actor who performs the cancel action.
				 * @param appointmentBelonger who belongs the appointment that are going to be cancelled.
				 * @param date appointment date
				 * @param serviceName appointment service name
				 * @param startTimeString appointment start time 
				 * @throws InvalidInputException input parameters are invalid
				 * @author Junjian Chen
				 */
				public static void cancelAppointment(String actor, String appointmentBelonger, String date, String serviceName, String startTimeString) throws InvalidInputException {
					String error="";
					
					User myUser=CarShopApplication.findUserFromUsername(actor);
					Customer belonger = CarShopApplication.findCustomer(appointmentBelonger);
					BookableService myBS=CarShopApplication.findBookableService(serviceName);
					Date myDate=Date.valueOf(date);
					Time startTime=Time.valueOf(startTimeString+":00");
					
					 if(myDate.before(CarShopApplication.getCarShop().getCurrentDate())||myDate.compareTo(CarShopApplication.getCarShop().getCurrentDate())==0) {
						 error+="Cannot cancel an appointment on the appointment date";
					 }
					//---------------	 
					if(myUser instanceof Technician) {
						 error+="A technician cannot cancel an appointment";
					}
					if(myUser instanceof Owner) {
						error+="An owner cannot cancel an appointment";
					}
					if (error.length() > 0) {
						throw new InvalidInputException(error.trim());
					}
					
					Customer myCustomer=CarShopApplication.findCustomer(myUser.getUsername());	
					if(!myCustomer.equals(belonger)) {
						throw new InvalidInputException("A customer can only cancel their own appointments");
					}
					
					 try {
						 if(myCustomer!=null) {
							 Appointment a=CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(myCustomer, myBS, myDate,startTime);
							if(a.getServiceBookings().get(0).getTimeSlot().getStartTime().compareTo(startTime)==0) {
								a.delete();
								CarShopPersistence.save(CarShopApplication.getCarShop());
							}
						 }
					 }catch (RuntimeException e) {
							throw new InvalidInputException(e.getMessage());
					 }
				}
				/**
				 * --------------------------------------------------------------------------------------------------------
				 * Deliverable 3: Appointment Management Controller Methods
				 * --------------------------------------------------------------------------------------------------------
				 */
				
				
				/**
				 *  Update the service or start date or start time of a appointment of a single service
                 * Or update all of them
                 * @author Junjian Chen, Shichang Zhang
				 * @param a appointment
				 * @param user user, need to be a consumer
				 * @param newService tried updated service
				 * @param startDate tried updated start date, in the form of standard date string
				 * @param startTime tried updated start time, string in the form of "hh:mm:ss"
				 */
				public static void updateServiceOfAppointment(Appointment a,String user,String newService,String startDate, String startTime) {
					User currUser = CarShopApplication.findCustomer(user);
					if(!a.getCustomer().equals(currUser)) return;
					CarShopApplication.setCurrentUser(currUser);
					Date date = Date.valueOf(startDate);
					Time start = Time.valueOf(startTime);
					Service service=CarShopApplication.findService(newService);
					a.UpdateService(service, date,start);
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				/**
				 * Update the optional services(by input a list), start date, start times(by input of a list) of a combo appointment
				 * Or update all of them
				 * @author Shichang Zhang
				 * @param a appointment
				 * @param user user, need to be a consumer
				 * @param optianalService optional service name. If don't add optionalService, the input string should be null
				 * @param opServiceTime start time string in form of "hh:mm". If don't add optional service, the input string should be null
				 * @param startDate tried updated start date, if time does't change, input original appointment start date.
				 * @param startTimeList tried updated start time. If time doesn't change, input the string of original list of start time.
				 */
				public static void updateServiceComboOfAppointment(Appointment a,String user,String optianalService, String opServiceTime,String startDate, String startTimeList) {
					User currUser = CarShopApplication.findCustomer(user);
					if(!a.getCustomer().equals(currUser)) return;
					CarShopApplication.setCurrentUser(currUser);
					
					Date date = Date.valueOf(startDate);
					
					List<Service> serviceList = new ArrayList<Service>();
					for(ServiceBooking sb : a.getServiceBookings()) {
						serviceList.add(sb.getService());
					}
					
					String[] timeStringList=startTimeList.split(",");
					List<Time> timeList=new ArrayList<Time>();
					for(int i=0;i<timeStringList.length;i++) {
						timeList.add(Time.valueOf(timeStringList[i]+":00"));
					}
					
					if(optianalService!=null && opServiceTime!=null) {
						serviceList.add(CarShopApplication.findServiceWithName(optianalService));
						timeList.add(Time.valueOf(opServiceTime+":00"));
					}
					System.out.println(serviceList.size()+"  "+timeList.size());
					a.UpdateServiceComboAppointment(serviceList,date,timeList);
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				
				/**
				 * @author Shichang Zhang
				 * @author Junjian Chen
				 * Update a appointment's startDate and Time depends on the abstract of bookable service
				 * @param a appointment
				 * @param user user
				 * @param newDate tried update start date, in the form of standard date string
				 * @param newTime tried update start time (list). If updated appointment is a service, input the start time string. If updated appointment is a service combo, input the string that is the list of start times. 
				 */
				public static void updateDateAndTime(Appointment a,String user, String newDate,String newTime) {
					if(a.getBookableService() instanceof Service) {
						//need to update date or time of a service appointment
						updateServiceOfAppointment(a,user,a.getBookableService().getName(),newDate,newTime+":00");
						CarShopPersistence.save(CarShopApplication.getCarShop());
					}else if (a.getBookableService() instanceof ServiceCombo) {
						//need to update date or time of a service combo appointment
						updateServiceComboOfAppointment(a,user,null,null,newDate,newTime);
						CarShopPersistence.save(CarShopApplication.getCarShop());
						//a.UpdateDateAndTime(Date.valueOf(newDate), timeList);
					}
					
				}
				
				/**
				 * @author Junjian Chen
				 * The author customer tries to cancel the appointment
				 * If it is on the date of appointment, cancel will fail
				 * If it is appointment is in progress, cancel will fail
				 * @param a appointment
				 * @param user user
				 */
				public static void endAppointment (Appointment a,String user) throws InvalidInputException{
					Customer c = CarShopApplication.findCustomer(user);
					if(c==null) throw new InvalidInputException("Only customer can cancel their own appointment");
					if(!c.equals(a.getCustomer()))throw new InvalidInputException("Only customer can cancel their own appointment");
					a.cancelAppointment();
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}

				
				/**
				 * Owner starts the appointment
				 * Transfer the status from BeforeAppDate to StartApp
				 * @param a appointment
				 */
				public static void ownerStartApp(Appointment a) throws InvalidInputException{
					if(!CarShopApplication.getCurrentUser().equals(CarShopApplication.getCarShop().getOwner())) {
						throw new InvalidInputException("Only owner can perform this action");
					}
					a.OnDate();
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				
				/**
				 * @author Junjian Chen
				 * Owner attempts to register a not-show-up record on the customer
				 * if the current is within the appointment's time slot, it will be not registered.
				 * @param a appointment
				 */
				public static void notShowUp(Appointment a) throws InvalidInputException{
					if(!CarShopApplication.getCurrentUser().equals(CarShopApplication.getCarShop().getOwner())) {
						throw new InvalidInputException("Only owner can perform this action");
					}
					a.OnDate();
					a.NotShowUp();
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				/**
				 * @author Junjian Chen
				 * Owner cancel the appointment normally as the appointment reaches its endTime
				 * If the appointment does not reach its startTime, cancel will fail
				 * @param a appointment
				 */
				public static void ownerEndAppNormally(Appointment a) throws InvalidInputException{
					if(!CarShopApplication.getCurrentUser().equals(CarShopApplication.getCarShop().getOwner())) {
						throw new InvalidInputException("Only owner can perform this action");
					}
					a.OnDate();
					a.TimeEnd();
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				
/*------------------------------------------------------------------------------*/

/*--------------------------------------UI helper methods-----------------------------------------*/			
	/**
	 * get the transfer object list of services in the system
	 * @return the service transfer object list
	 */
	public static ArrayList<TOService> getServices() {
		CarShop carShop = CarShopApplication.getCarShop();
		ArrayList<TOService> toServices = new ArrayList<TOService>();
		for(BookableService bs : carShop.getBookableServices()) {
			if(bs instanceof Service) {
				Service s = (Service)bs;
				TOService toService = new TOService(s.getName());
				toServices.add(toService);
			}
		}
		return toServices;
	}			

	
	public static ArrayList<TOAppointment> getServiceAppointments(){
		CarShop carShop = CarShopApplication.getCarShop();
		ArrayList<TOAppointment> toAppointments = new ArrayList<TOAppointment>();
		for(Appointment a : carShop.getAppointments()) {
			if(a.getBookableService() instanceof Service) {
				int id=carShop.getAppointments().indexOf(a);
				String customerName = a.getCustomer().getUsername();
				String serviceName = a.getBookableService().getName();
				String startDate = a.getServiceBookings().get(0).getTimeSlot().getStartDate().toString();
				String startTimeTemp = a.getServiceBookings().get(0).getTimeSlot().getStartTime().toString();
				String startTime = startTimeTemp.substring(0,startTimeTemp.length()-3);
				String endTimeTemp = a.getServiceBookings().get(0).getTimeSlot().getEndTime().toString();
				String endTime = startTimeTemp.substring(0,endTimeTemp.length()-3);
				TOAppointment toA = new TOAppointment(a.hashCode(), customerName,serviceName,startDate,startTime, endTime);
				toAppointments.add(toA);
			}
		}
		return toAppointments;
	}
	
	
	/**
	 * User clicks the start button of appointment
	 * @param toA transfer object of the appointment to be started
	 * @throws InvalidInputException
	 */
	public static void updateServiceOfAppointmentUI(TOAppointment toA,String user, String newService, String startDayString,String startTimeString) throws InvalidInputException{
		Customer c = CarShopApplication.findCustomer(toA.getCustomerName());
		BookableService bs = CarShopApplication.findBookableService(toA.getBookableServiceName());
		Date d = Date.valueOf(toA.getStartDate());
		Time startTime = Time.valueOf(toA.getStartTime()+":00");
		Appointment a = CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(c, bs, d,startTime);
		if(a==null) throw new InvalidInputException("No matched appointment exist in the system");
		updateServiceOfAppointment(a,user,newService,startDayString,startTimeString+":00");
	}
	
	
	/**
	 * User clicks the start button of appointment
	 * @param toA transfer object of the appointment to be started
	 * @throws InvalidInputException
	 */
	public static void ownerStartAppUI(TOAppointment toA) throws InvalidInputException{
		Customer c = CarShopApplication.findCustomer(toA.getCustomerName());
		BookableService bs = CarShopApplication.findBookableService(toA.getBookableServiceName());
		Date d = Date.valueOf(toA.getStartDate());
		Time startTime = Time.valueOf(toA.getStartTime()+":00");
		Appointment a = CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(c, bs, d,startTime);
		if(a==null) throw new InvalidInputException("No matched appointment exist in the system");
		ownerStartApp(a);
	}
	
	/**
	 * User clicks the end button of appointment
	 * @param toA transfer object of the appointment to be started
	 * @throws InvalidInputException
	 */
	public static void ownerEndAppNormallyUI(TOAppointment toA) throws InvalidInputException{
		Customer c = CarShopApplication.findCustomer(toA.getCustomerName());
		BookableService bs = CarShopApplication.findBookableService(toA.getBookableServiceName());
		Date d = Date.valueOf(toA.getStartDate());
		Time startTime = Time.valueOf(toA.getStartTime()+":00");
		Appointment a = CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(c, bs, d,startTime);
		if(a==null) throw new InvalidInputException("No matched appointment exist in the system");
		ownerEndAppNormally(a);
	}
	
	/**
	 * User clicks the no show button when appointment is in the process
	 * @param toA transfer object of the appointment to be started
	 * @throws InvalidInputException
	 */
	public static void notShowUpUI(TOAppointment toA) throws InvalidInputException{
		Customer c = CarShopApplication.findCustomer(toA.getCustomerName());
		BookableService bs = CarShopApplication.findBookableService(toA.getBookableServiceName());
		Date d = Date.valueOf(toA.getStartDate());
		Time startTime = Time.valueOf(toA.getStartTime()+":00");
		Appointment a = CarShopApplication.findAppByCustomerAndBookableServiceAndDateAndTime(c, bs, d,startTime);
		if(a==null) throw new InvalidInputException("No matched appointment exist in the system");
		notShowUp(a);
	}
	/**
	 * Gets a list of appointment transfer objects by date
	 * @author Junjian Chen
	 * @param date A given date
	 * @return A list of a list of appointment transfer objects of the appointments on that date
	 */
	public static List<TOAppointment> getAppOverview(Date date) {
		CarShop cs=CarShopApplication.getCarShop();
		ArrayList<TOAppointment> appList=new ArrayList<TOAppointment>();
		for(Appointment a:cs.getAppointments()) {
			if(a.getServiceBookings().get(0).getTimeSlot().getStartDate()!=null) {
				if(a.getServiceBookings().get(0).getTimeSlot().getStartDate().compareTo(date)==0) {
					TOAppointment ta=new TOAppointment(a.hashCode(), a.getCustomer().getUsername(), a.getBookableService().getName(), date.toString(),a.getServiceBookings().get(0).getTimeSlot().getStartTime().toString(), a.getServiceBookings().get(0).getTimeSlot().getEndTime().toString());
					appList.add(ta);
				}
			}
			
		}
		return appList;		
	}	
	/**
	 * Get the current user's transfer object
	 * @author Junjian Chen
	 * @return current user's transfer object
	 */			
	public static TOUser getTOCurrentUser() {
		if(CarShopApplication.getCurrentUser()==null) {
			return null;
		}else {
			TOUser a=new TOUser(CarShopApplication.getCurrentUser().getUsername());
			return a;
		}
		
	}
	
	public static TOBusiness getCurrentTOBusiness() {
		Business b=CarShopApplication.getCarShop().getBusiness();
		if(b!=null) {
			TOBusiness item=new TOBusiness(b.getName(), b.getAddress(), b.getPhoneNumber(), b.getEmail());
			return item;
		}else {
			return null;
		}	
	}
		
	
	public static TOBusinessHour getTOBusinessHour(String weekday) {
		if(CarShopApplication.getCarShop().getBusiness()!=null) {
			for(BusinessHour bs:CarShopApplication.getCarShop().getBusiness().getBusinessHours()) {
				if(bs.getDayOfWeek()!=null) {
						if(bs.getDayOfWeek().equals(CarShopApplication.getDayOfWeekFromString(weekday))) {
								return new TOBusinessHour(bs.getStartTime().toString().substring(0, bs.getStartTime().toString().length()-3),bs.getEndTime().toString().substring(0, bs.getEndTime().toString().length()-3));
						}
				}

			}	
		}
		return null;
	}

	public static TOGarageOH getTOGarageOH(String weekday,String garage) {

		Garage g=CarShopApplication.getGarageFromTechnicianType(CarShopApplication.getTechnicianTypeFromString(garage));
		if(g!=null) {
			for(BusinessHour bh:g.getBusinessHours()) {
				if(bh.getDayOfWeek()!=null) {
					if(bh.getDayOfWeek().equals(CarShopApplication.getDayOfWeekFromString(weekday))) {
					return new TOGarageOH(bh.getStartTime().toString().substring(0, bh.getStartTime().toString().length()-3),bh.getEndTime().toString().substring(0, bh.getEndTime().toString().length()-3));
				}
				}

			}
		}
		return null;
	}
	
	public static String getCurrentTime() {
		String dateAndTime;
		dateAndTime=CarShopApplication.getCarShop().getCurrentDate().toString()+"+"+CarShopApplication.getCarShop().getCurrentTime().toString().substring(0, CarShopApplication.getCarShop().getCurrentTime().toString().length()-3);
		return dateAndTime;
	}
	
	public static String getStatus() {
		if(CarShopApplication.getCarShop().getAppointments().size()!=0) {
			return CarShopApplication.getCarShop().getAppointments().get(0).getStatus().toString();
		}
		return "No Appointment";
	}
/*--------------------------------------UI helper methods-----------------------------------------*/	
	

	
	public static void testAddAppointment() {
		Service s=new Service("electronics-change",CarShopApplication.getCarShop(), 15, new Garage(CarShopApplication.getCarShop(), new Technician("aaa", "aaa", TechnicianType.Electronics, CarShopApplication.getCarShop())));
		Appointment a=CarShopApplication.getCarShop().addAppointment(new Customer("Bob11111", "caa", CarShopApplication.getCarShop()),s);
		a.addServiceBooking(s, new TimeSlot(Date.valueOf("1111-11-11"), (Time.valueOf("11:11:11")), Date.valueOf("1111-11-11"), (Time.valueOf("11:11:11")), CarShopApplication.getCarShop()));
	}
				
/*-------------------------------presentation helper methods----------------------------------*/
				public static void setTime(String dateAndTimeString) {
					 String[] dtArray=dateAndTimeString.split("\\+");
					 Date currentDate=Date.valueOf(dtArray[0]);
					 Time cuurentTime=Time.valueOf(dtArray[1]+":00");
					 CarShopApplication.getCarShop().setCurrentDate(currentDate);
					 CarShopApplication.getCarShop().setCurrentTime(cuurentTime);
				}
				
				public static void initializeGarage() {
					for(Technician t:CarShopApplication.getCarShop().getTechnicians()) {
						if(t.hasGarage()==false) {
							CarShopApplication.getCarShop().addGarage(t);
						}
					}
					CarShopPersistence.save(CarShopApplication.getCarShop());
				}
				
				public static void customerSignUp(String username, String password) throws Exception {
					boolean usernameExist=false;
					for(Customer customer : CarShopApplication.getCarShop().getCustomers()) {
						if(customer.getUsername().equals(username)) {
							usernameExist = true;					
							break;
						}
					}
					
					if(usernameExist==false) {
						Customer a=CarShopApplication.getCarShop().addCustomer(username, password);
						a.setTimeNotShow(0);
					}else {
						throw new Exception("Username has already exists!");
					}
				}
				
				public static boolean signInPasswordVerification(String password,String confirmPassword) {
					return password.equals(confirmPassword);
				}
	}