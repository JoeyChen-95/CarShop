/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 23 "../../../../../CarshopTransferObjects.ump"
public class TOAppointment
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOAppointment Attributes
  private int appNum;
  private String customerName;
  private String bookableServiceName;
  private String startDate;
  private String startTime;
  private String endTime;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOAppointment(int aAppNum, String aCustomerName, String aBookableServiceName, String aStartDate, String aStartTime, String aEndTime)
  {
    appNum = aAppNum;
    customerName = aCustomerName;
    bookableServiceName = aBookableServiceName;
    startDate = aStartDate;
    startTime = aStartTime;
    endTime = aEndTime;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setAppNum(int aAppNum)
  {
    boolean wasSet = false;
    appNum = aAppNum;
    wasSet = true;
    return wasSet;
  }

  public boolean setCustomerName(String aCustomerName)
  {
    boolean wasSet = false;
    customerName = aCustomerName;
    wasSet = true;
    return wasSet;
  }

  public boolean setBookableServiceName(String aBookableServiceName)
  {
    boolean wasSet = false;
    bookableServiceName = aBookableServiceName;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartDate(String aStartDate)
  {
    boolean wasSet = false;
    startDate = aStartDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartTime(String aStartTime)
  {
    boolean wasSet = false;
    startTime = aStartTime;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndTime(String aEndTime)
  {
    boolean wasSet = false;
    endTime = aEndTime;
    wasSet = true;
    return wasSet;
  }

  public int getAppNum()
  {
    return appNum;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public String getBookableServiceName()
  {
    return bookableServiceName;
  }

  public String getStartDate()
  {
    return startDate;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public String getEndTime()
  {
    return endTime;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "appNum" + ":" + getAppNum()+ "," +
            "customerName" + ":" + getCustomerName()+ "," +
            "bookableServiceName" + ":" + getBookableServiceName()+ "," +
            "startDate" + ":" + getStartDate()+ "," +
            "startTime" + ":" + getStartTime()+ "," +
            "endTime" + ":" + getEndTime()+ "]";
  }
}