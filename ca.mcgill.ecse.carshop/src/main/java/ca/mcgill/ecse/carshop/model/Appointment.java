/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.model;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.*;

// line 1 "../../../../../CarShopState.ump"
// line 107 "../../../../../CarShopPersistence.ump"
// line 187 "../../../../../carshop.ump"
public class Appointment implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Appointment State Machines
  public enum Status { Off, BeforeAppDate, StartApp }
  private Status status;

  //Appointment Associations
  private Customer customer;
  private BookableService bookableService;
  private List<ServiceBooking> serviceBookings;
  private CarShop carShop;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Appointment(Customer aCustomer, BookableService aBookableService, CarShop aCarShop)
  {
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create appointment due to customer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBookableService = setBookableService(aBookableService);
    if (!didAddBookableService)
    {
      throw new RuntimeException("Unable to create appointment due to bookableService. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    serviceBookings = new ArrayList<ServiceBooking>();
    boolean didAddCarShop = setCarShop(aCarShop);
    if (!didAddCarShop)
    {
      throw new RuntimeException("Unable to create appointment due to carShop. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    setStatus(Status.Off);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public String getStatusFullName()
  {
    String answer = status.toString();
    return answer;
  }

  public Status getStatus()
  {
    return status;
  }

  public boolean make()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Off:
        setStatus(Status.BeforeAppDate);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean cancelAppointment()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case BeforeAppDate:
        if (!(isOnDate()))
        {
        // line 10 "../../../../../CarShopState.ump"
          cancelApp();
          setStatus(Status.Off);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean OnDate()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case BeforeAppDate:
        setStatus(Status.StartApp);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean UpdateDateAndTime(Date startDate,List<Time> timeList)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case BeforeAppDate:
        if (!(isOnDate()))
        {
        // line 12 "../../../../../CarShopState.ump"
          changeDateAndTime(startDate,timeList);
          setStatus(Status.BeforeAppDate);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean UpdateService(Service service,Date startDate,Time startTime)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case BeforeAppDate:
        if (!(isOnDate())&&(isTimeAvailable(service,startDate,startTime)))
        {
        // line 13 "../../../../../CarShopState.ump"
          changeSingleServiceAppointment(service,startDate,startTime);
          setStatus(Status.BeforeAppDate);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean UpdateServiceComboAppointment(List<Service> serviceList,Date startDate,List<Time> startTimeList)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case BeforeAppDate:
        if ((!(isOnDate()))&&(areTimesAvailable(serviceList,startDate,startTimeList)))
        {
        // line 14 "../../../../../CarShopState.ump"
          changeComboAppointment(serviceList,startDate,startTimeList);
          setStatus(Status.BeforeAppDate);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean NotShowUp()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case StartApp:
        if (!(isInProgress()))
        {
        // line 21 "../../../../../CarShopState.ump"
          customerNotShow();
          setStatus(Status.Off);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean TimeEnd()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case StartApp:
        if (isInProgress())
        {
        // line 22 "../../../../../CarShopState.ump"
          cancelApp();
          setStatus(Status.Off);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setStatus(Status aStatus)
  {
    status = aStatus;
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }
  /* Code from template association_GetOne */
  public BookableService getBookableService()
  {
    return bookableService;
  }
  /* Code from template association_GetMany */
  public ServiceBooking getServiceBooking(int index)
  {
    ServiceBooking aServiceBooking = serviceBookings.get(index);
    return aServiceBooking;
  }

  public List<ServiceBooking> getServiceBookings()
  {
    List<ServiceBooking> newServiceBookings = Collections.unmodifiableList(serviceBookings);
    return newServiceBookings;
  }

  public int numberOfServiceBookings()
  {
    int number = serviceBookings.size();
    return number;
  }

  public boolean hasServiceBookings()
  {
    boolean has = serviceBookings.size() > 0;
    return has;
  }

  public int indexOfServiceBooking(ServiceBooking aServiceBooking)
  {
    int index = serviceBookings.indexOf(aServiceBooking);
    return index;
  }
  /* Code from template association_GetOne */
  public CarShop getCarShop()
  {
    return carShop;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    if (aCustomer == null)
    {
      return wasSet;
    }

    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeAppointment(this);
    }
    customer.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBookableService(BookableService aBookableService)
  {
    boolean wasSet = false;
    if (aBookableService == null)
    {
      return wasSet;
    }

    BookableService existingBookableService = bookableService;
    bookableService = aBookableService;
    if (existingBookableService != null && !existingBookableService.equals(aBookableService))
    {
      existingBookableService.removeAppointment(this);
    }
    bookableService.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfServiceBookings()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ServiceBooking addServiceBooking(Service aService, TimeSlot aTimeSlot)
  {
    return new ServiceBooking(aService, aTimeSlot, this);
  }

  public boolean addServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasAdded = false;
    if (serviceBookings.contains(aServiceBooking)) { return false; }
    Appointment existingAppointment = aServiceBooking.getAppointment();
    boolean isNewAppointment = existingAppointment != null && !this.equals(existingAppointment);
    if (isNewAppointment)
    {
      aServiceBooking.setAppointment(this);
    }
    else
    {
      serviceBookings.add(aServiceBooking);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasRemoved = false;
    //Unable to remove aServiceBooking, as it must always have a appointment
    if (!this.equals(aServiceBooking.getAppointment()))
    {
      serviceBookings.remove(aServiceBooking);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {  
    boolean wasAdded = false;
    if(addServiceBooking(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {
    boolean wasAdded = false;
    if(serviceBookings.contains(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addServiceBookingAt(aServiceBooking, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCarShop(CarShop aCarShop)
  {
    boolean wasSet = false;
    if (aCarShop == null)
    {
      return wasSet;
    }

    CarShop existingCarShop = carShop;
    carShop = aCarShop;
    if (existingCarShop != null && !existingCarShop.equals(aCarShop))
    {
      existingCarShop.removeAppointment(this);
    }
    carShop.addAppointment(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeAppointment(this);
    }
    BookableService placeholderBookableService = bookableService;
    this.bookableService = null;
    if(placeholderBookableService != null)
    {
      placeholderBookableService.removeAppointment(this);
    }
    while (serviceBookings.size() > 0)
    {
      ServiceBooking aServiceBooking = serviceBookings.get(serviceBookings.size() - 1);
      aServiceBooking.delete();
      serviceBookings.remove(aServiceBooking);
    }
    
    CarShop placeholderCarShop = carShop;
    this.carShop = null;
    if(placeholderCarShop != null)
    {
      placeholderCarShop.removeAppointment(this);
    }
  }


  /**
   * check if current date is on the date of appointment
   */
  // line 28 "../../../../../CarShopState.ump"
   private boolean isOnDate(){
    if(this.getServiceBookings().get(0).getTimeSlot().getStartDate().compareTo(carShop.getCurrentDate())==0){
		  return true;
	  }else {
		  return false;
	  }
  }


  /**
   * cancel the appointment
   */
  // line 36 "../../../../../CarShopState.ump"
   private void cancelApp(){
    this.delete();
  }


  /**
   * change the date of time of a appointment, depends on its abstract of bookable service
   */
  // line 40 "../../../../../CarShopState.ump"
   private void changeDateAndTime(Date startDate, List<Time> timeList){
    if(this.getBookableService() instanceof Service) {
		 Service s=(Service)this.getBookableService();
		 TimeSlot ts = this.getServiceBookings().get(0).getTimeSlot();
		 if(startDate!=null) {
			ts.setStartDate(startDate);
			ts.setEndDate(startDate); 
		 }
		 if(timeList.size()!=0) {
			ts.setStartTime(timeList.get(0));
			ts.setEndTime(carShop.afterTime(timeList.get(0).toString(), s.getDuration()));
		 }
		 
	 }else {
		 ServiceCombo sc=(ServiceCombo)this.getBookableService();
		 for(ServiceBooking sb:this.getServiceBookings()) {
			 TimeSlot ts=sb.getTimeSlot();
			 int index=this.getServiceBookings().indexOf(sb);
			 if(startDate!=null) {
				 ts.setStartDate(startDate);
				 ts.setEndDate(startDate); 
			 }
			 if(timeList.size()!=0) {
				 ts.setStartTime(timeList.get(index));
				 ts.setEndTime(carShop.afterTime(timeList.get(index).toString(), this.getServiceBookings().get(index).getService().getDuration()));
			 }
		 }
	 }
  }


  /**
   * Only change the service of a single service appointment, also update timeslot correspondingly
   */
  // line 70 "../../../../../CarShopState.ump"
   private void changeService(Service service){
    if(service!=null) {
    	TimeSlot original = this.getServiceBooking(0).getTimeSlot();
		  this.setBookableService(service);
		  original.setEndTime(carShop.afterTime(original.getStartTime().toString(), service.getDuration()));
	  }
  }


  /**
   * customer does not show up for the appointment
   */
  // line 78 "../../../../../CarShopState.ump"
   private void customerNotShow(){
    this.getCustomer().setTimeNotShow(this.getCustomer().getTimeNotShow()+1);
	  this.delete();
  }


  /**
   * check whether the techician is available of a newservice
   */
  // line 83 "../../../../../CarShopState.ump"
   public boolean isTechinicianAvailable(Service service){
    for(BusinessHour bh:service.getGarage().getBusinessHours()) {
		  if(bh.getDayOfWeek().equals(carShop.getDayOfWeekFromString(carShop.getCurrentDate().toString()))) {
			  return true;
		  }
	  }
	   return false;
  }


  /**
   * Check whether time conflict exists when adding a new service to the appointment
   */
  // line 93 "../../../../../CarShopState.ump"
   private boolean isTimeAvailable(Service service, Date startDate, Time startTime){
    Time endTime = carShop.afterTime(startTime.toString(), service.getDuration());
	  
	  //check whether the time is in the holiday and vacation
	  for(TimeSlot s:carShop.getBusiness().getHolidays()) {
		  boolean startOverlap = startDate.compareTo(s.getStartDate())>=0 && startDate.compareTo(s.getEndDate())<=0;
		  if(startOverlap) return false;
	  }
	  for(TimeSlot s:carShop.getBusiness().getVacations()) {
		  boolean startOverlap = startDate.compareTo(s.getStartDate())>=0 && startDate.compareTo(s.getEndDate())<=0;
		  if(startOverlap) return false;
	  }
	  
	  //check whether the time is in the opening hour
	  for(BusinessHour bs : carShop.getBusiness().getBusinessHours()) {
		  if(bs.getDayOfWeek().equals(carShop.getWeek(startDate))){
			  if(startTime.before(bs.getStartTime())||endTime.after(bs.getEndTime())) {
				  return false;
			  }
		  }
	  }
	  
	  //check whether is in the garage opening hour
	  for(BusinessHour bs : carShop.getGarages().get(0).getBusinessHours()) {
		  if(bs.getDayOfWeek().equals(carShop.getWeek(startDate))){
			  if(startTime.before(bs.getStartTime())||endTime.after(bs.getEndTime())) {
				  return false;
			  }
		  }
	  }
	  
	  //check whether overlap with other appointment
	  for(Appointment a : carShop.getAppointments()) {
		  if(!a.equals(this)) {
			  for(ServiceBooking s : a.getServiceBookings()) {
				  if(s.getTimeSlot().getStartDate().equals(startDate) && s.getService().getName().equals(service.getName())) {
					  boolean startOverlap = startTime.after(s.getTimeSlot().getStartTime()) && startTime.before(s.getTimeSlot().getEndTime());
						boolean endOverlap = endTime.after(s.getTimeSlot().getStartTime()) && endTime.before(s.getTimeSlot().getEndTime());
						if(startOverlap||endOverlap) return false;
				  }
			  }
		  }
	  }
	  
	  return true;
  }


  /**
   * Check whether time conflict exists when adding a list of new services to the appointment
   */
  // line 141 "../../../../../CarShopState.ump"
   private boolean areTimesAvailable(List<Service> serviceList, Date startDate, List<Time> startTimeList){
    for(int i = 0; i<serviceList.size();i++) {
		   boolean flag =isTimeAvailable(serviceList.get(i),startDate,startTimeList.get(i));
		   if(!flag) return false;
	   }
	   return true;
  }


  /**
   * Add optional service while in progress
   */
  // line 149 "../../../../../CarShopState.ump"
   private void changeServiceAppointment(Service service, Date startDate, Time startTime){
    ServiceCombo sc=(ServiceCombo)this.getBookableService();
	sc.addService(new ComboItem(false, service, sc));
    TimeSlot ts=new TimeSlot(startDate, startTime, startDate, this.carShop.afterTime(startTime.toString(), service.getDuration()), carShop);
    this.addServiceBooking(service, ts);
  }


  /**
   * Update the optional services(by input a list), start date, start times(by input of a list) of a combo appointment
   * Or update all of them
   */
  // line 157 "../../../../../CarShopState.ump"
   private void changeComboAppointment(List<Service> serviceList, Date startDate, List<Time> startTimeList){
    ServiceCombo sc=(ServiceCombo)this.getBookableService();
	//check whether add new optional service
    if(sc.getServices().size()+1<serviceList.size()) {
    	Service service = serviceList.get(serviceList.size()-1);
    	Time startTime = startTimeList.get(startTimeList.size()-1);
    	sc.addService(new ComboItem(false,service, sc));
    	TimeSlot ts=new TimeSlot(startDate, startTime, startDate, this.carShop.afterTime(startTime.toString(), service.getDuration()), carShop);
        this.addServiceBooking(service, ts);
    }
	this.changeDateAndTime(startDate, startTimeList);
  }


  /**
   * Update the service or start date or start time of a appointment of a single service
   * Or update all of them
   */
  // line 171 "../../../../../CarShopState.ump"
   private void changeSingleServiceAppointment(Service service, Date startDate, Time startTime){
    this.changeService(service);
	   List<Time> t=new ArrayList<Time>();
	   t.add(startTime);
	   this.changeDateAndTime(startDate, t);
  }


  /**
   * check whether the current time is within the appointment timeslot
   * e.g. if a app startTime is 10:00 and endTime is 10:20
   * currentTime from 10:00(Included!) to 10:20(Included!) will return true
   */
  // line 180 "../../../../../CarShopState.ump"
   private boolean isInProgress(){
    Time curTime=this.carShop.getCurrentTime();
	   for(ServiceBooking sb:this.getServiceBookings()) {
		   TimeSlot ts=sb.getTimeSlot();
		   if(curTime.compareTo(ts.getStartTime())>=0&&curTime.compareTo(ts.getEndTime())<=0) {
			   return true;
		   }
	   }
	   return false;
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 110 "../../../../../CarShopPersistence.ump"
  private static final long serialVersionUID = -2683593616927798013L ;

  
}