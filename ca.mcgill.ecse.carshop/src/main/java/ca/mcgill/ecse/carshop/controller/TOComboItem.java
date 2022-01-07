/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 7 "../../../../../CarshopTransferObjects.ump"
public class TOComboItem
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOComboItem Attributes
  private String name;
  private boolean mandatory;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOComboItem(String aName, boolean aMandatory)
  {
    name = aName;
    mandatory = aMandatory;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setMandatory(boolean aMandatory)
  {
    boolean wasSet = false;
    mandatory = aMandatory;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public boolean getMandatory()
  {
    return mandatory;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "mandatory" + ":" + getMandatory()+ "]";
  }
}