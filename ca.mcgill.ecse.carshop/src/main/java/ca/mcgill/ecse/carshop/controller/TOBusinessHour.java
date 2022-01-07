/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 36 "../../../../../CarshopTransferObjects.ump"
public class TOBusinessHour
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOBusinessHour Attributes
  private String startTime;
  private String endTime;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOBusinessHour(String aStartTime, String aEndTime)
  {
    startTime = aStartTime;
    endTime = aEndTime;
  }

  //------------------------
  // INTERFACE
  //------------------------

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
            "startTime" + ":" + getStartTime()+ "," +
            "endTime" + ":" + getEndTime()+ "]";
  }
}