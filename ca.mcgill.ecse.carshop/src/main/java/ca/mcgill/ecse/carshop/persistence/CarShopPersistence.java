package ca.mcgill.ecse.carshop.persistence;

import ca.mcgill.ecse.carshop.model.CarShop;

public class CarShopPersistence {
private static String filename = "data.carShop";
	
	public static void setFilename(String filename) {
		CarShopPersistence.filename = filename;
	}
	
	public static void save(CarShop carShop) {
	    PersistenceObjectStream.serialize(carShop);
	}

	public static CarShop load() {
	    PersistenceObjectStream.setFilename(filename);
	    CarShop carShop = (CarShop) PersistenceObjectStream.deserialize();
	    // model cannot be loaded - create empty carshop
	    if (carShop == null) {
	    	carShop = new CarShop();
	    }
	    else {
	    	carShop.reinitialize();
	    }
	    return carShop;
	}
}
