package ca.mcgill.ecse.carshop.view;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.TOAppointment;
import ca.mcgill.ecse.carshop.controller.TOService;
/**
 * This class is the User Inteface(UI) of the Project.
 * @author Junjian Chen
 * @author Shichang Zhang
 * @author John Wang
 * @author Yuyan Shi
 *
 */
public class CarShopPage extends JFrame {
	private String error;
	private String success;
	private JLabel errorMessage;
	private HashMap<Integer, TOAppointment> toAppointmentMap ;
	private JLabel currentTimeLabel;
	private JTextField currentTimeField;
	private JButton currentTimeSetButton;
	//Log In------------------------------------------------------------------------------------------------------------------
	private JLabel userTitle;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JButton signUpButton;
	private JButton loginButton;
	private JLabel currentUserLabel;
	private JLabel currentUserDisplayLabel;
	private JButton logoutButton;
	private JButton updateAccountButton;
	//Set/Update Business Information------------------------------------------------------------------------------------------------------------------
	private JLabel biTitle;
	private JLabel biCarShopNameLabel;
	private JLabel biEmailLabel;
	private JLabel biPhoneLabel;
	private JLabel biAddressLabel;
	private JButton biUpdateButton;

	private JTextField biCarShopNameField;
	private JTextField biEmailField;
	private JTextField biPhoneField;
	private JTextField biAddressField;
	//Set/Update Business Hour------------------------------------------------------------------------------------------------------------------
	private JLabel bhUpdateTitle;
	private JLabel bhTitle;
	private JLabel bhWeekDaysLabel;
	private JLabel bhStartTimeLabel;
	private JLabel bhEndTimeLabel;
	
	//private JLabel exsistingStartingTimeLabel;
	private JTextField bhStartTimeField;
	private JTextField bhEndTimeField;
	private JTextField bhUpdateOldStartTimeField;
	private JTextField bhUpdateNewStartTimeField;
	private JTextField bhUpdateNewEndTimeField;
	//private JTextField exsistingStartingTimeField;
	private JComboBox<String> bhSetWeekDaysBox;
	private HashMap<Integer, String> days;
	private JButton bhSetButton;
	private JButton bhUpdateButton;
	//Update Garage Opening Hour
	private JLabel garageOHSelectedLabel;
	private JComboBox<String> garageOHSelectedBox; 
	private HashMap<Integer, String> forOHselectedgarages;
	private JButton garageOHRemoveButton;
	private JButton garageOHAddButton;
	private JLabel garageOHStartTimeLabel;
	private JLabel garageOHEndTimeLabel;
	private JTextField garageOHStartTimeField;
	private JTextField garageOHEndTimeField;
	
	private JLabel bhUpdateOldStartTimeLabel;
	private JLabel bhUpdateOldDateLabel;
	private JLabel bhUpdateNewDateLabel;
	private JLabel bhUpdateNewStartTimeLabel;
	private JLabel bhUpdateNewEndTimeLabel;
	private JComboBox<String> bhUpdateOldWeekDaysBox;
	private HashMap<Integer, String> updateDays;
	private JComboBox<String> bhUpdateNewWeekDaysBoxDaysBox;
	private HashMap<Integer, String> targetDays;
	
	//Add service / Update service------------------------------------------------------------------------------------------------------------------
	private JLabel addServiceTitle;
	private JLabel updateServiceNameLabel;
	private JLabel updateServiceTitle;
	private JLabel serviceNameLabel;
	private JLabel serviceDurationLabel;
	private JLabel garageSelectedLabel;
	private JTextField serviceNameField;
	private JTextField serviceDurationField;
	private JComboBox<String> garageSeletedComboBox;
	private HashMap<Integer, String> targetGarages;
	private JButton addServiceButton;
	
	private JComboBox<String> serviceUpdateSelectedComboBox;
	private JLabel serviceSelectedLabel;
	private JLabel newNameLabel;
	private JLabel newDurationLabel;
	private JLabel newGarageLabel;
	private JTextField newServiceNameField;
	private JTextField newServiceDurationField;
	private JComboBox<String> newGarageSeletedComboBox;
	private HashMap<Integer, String> updateGarages;
	private JButton updateServiceButton;
	
	//Make Appointment------------------------------------------------------------------------------------------------------------------
	private JLabel makeAppTitle;
	private JLabel cancelAppTitle;
	private JLabel updateAppTitle;
	private JLabel appMakeDateLabel;
	private JLabel appStartTimeLabel;
	private JLabel appServiceSelectedLabel;
	private JTextField appYear;
	private JTextField appMonth;
	private JTextField appDay;
	private JTextField appStartTimeField;
	private JComboBox<String> appServiceSelectedBox;
	private JButton appMakeButton;
	//Cancel Appointment
	private JLabel appCancelDateLabel;
	private JLabel appCancelStartTimeLabel;
	private JLabel appCancelServiceSelectedLabel;
	private JTextField appCancelYear;
	private JTextField appCancelMonth;
	private JTextField appCancelDay;
	private JTextField appCancelStartTimeField;
	private JComboBox<String> appCancelServiceSelectedBox;
	private JButton appCancelButton;
	//Update Appointment
	private JLabel appUpdateNameLabel;
	private JLabel appUpdateServiceNameLabel;
	private JLabel appUpdateNewDateLabel;
	private JLabel appUpdateNewTimeLabel;
	private JComboBox<String> appUpdateServiceSelectedBox;
	private JComboBox<String> appSelectedBox;
	private JTextField appUpdateYear;
	private JTextField appUpdateMonth;
	private JTextField appUpdateDay;
	private JTextField appUpdateTimeField;
	private JButton appUpdateButton;
	//Start/End/Register No Show Appointment------------------------------------------------------------------------------------------------------------------
	private JLabel appOpTitle;
	private JLabel appOpSelectedLabel;
	private JButton appStartButton;
	private JButton appEndButton;
	private JButton appNotShowButton;
	private JComboBox<String> appOpSelectedBox;
	//View By Date
	// daily overview
	private JTable appViewTable;
	private JScrollPane appViewScrollPane;
	
	private DefaultTableModel overviewDtm;
	private String overviewColumnNames[] = {"Appointment#","Customer", "Service", "Date", "Start Time","End Time"};
	private static final int HEIGHT_OVERVIEW_TABLE = 200;
	
	private JLabel appViewDateLabel;
	private JTextField appViewYear;
	private JTextField appViewMonth;
	private JTextField appViewDay;
	private JButton appViewSearchByDateButton;
	//private JLabel 
	
	public CarShopPage() {
		initComponents();
		refreshData();
	}
	
	/**
	 * This method initialize the UI elements in the console.
	 * @author Junjian Chen
	 * @author Shichang Zhang
	 * @author John Wang
	 * @author Yuyan Shi
	 */
	private void initComponents() {
		error = "";
		success = "";
		setTitle("CarShop");
		//CarShopController.testAddAppointment();
		errorMessage = new JLabel();
		errorMessage.setText("Message: ");
		currentTimeLabel=new JLabel();
		currentTimeLabel.setText("Current Time:");
		currentTimeField=new JTextField();
		currentTimeSetButton=new JButton();
		currentTimeSetButton.setText("Set");
		//Log In------------------------------------------------------------------------------------------------------------------
		userTitle=new JLabel();
		userTitle.setText("Sign Up/Log in");
		usernameTextField = new JTextField(); 
		passwordTextField = new JTextField();
		usernameLabel = new JLabel();
		usernameLabel.setText("Username:");
		passwordLabel = new JLabel();
		passwordLabel.setText("Password:");
		loginButton = new JButton();
		loginButton.setText("Login");
		signUpButton=new JButton();
		signUpButton.setText("Sign Up");
		logoutButton=new JButton();
		logoutButton.setText("Log Out");
		currentUserLabel=new JLabel();
		currentUserLabel.setText("Current User: ");
		currentUserLabel.setForeground(Color.BLUE);
		currentUserDisplayLabel=new JLabel();
		currentUserDisplayLabel.setText("");
		updateAccountButton=new JButton();
		updateAccountButton.setText("Reset Username&Password");
		//Set/Update Business Information------------------------------------------------------------------------------------------------------------------
		biTitle=new JLabel();
		biTitle.setText("Set Business Information");
		biCarShopNameLabel = new JLabel();
		biCarShopNameLabel.setText("CarShopName:");
		biEmailLabel = new JLabel();
		biEmailLabel.setText("Email:");
		biPhoneLabel = new JLabel();
		biPhoneLabel.setText("Phone:");
		biAddressLabel = new JLabel();
		biAddressLabel.setText("Address:");
		biUpdateButton = new JButton();
		biUpdateButton.setText("Set/Update");

		biCarShopNameField = new JTextField(); 
		biEmailField = new JTextField();
		biPhoneField = new JTextField(); 
		biAddressField = new JTextField();
		//Set/Update Business Hour------------------------------------------------------------------------------------------------------------------
		bhUpdateTitle= new JLabel();
		bhUpdateTitle.setText("Update Business Hour");
		bhTitle=new JLabel();
		bhTitle.setText("Set Business Hour/Garage Opening Hour");
		bhWeekDaysLabel = new JLabel();
		bhWeekDaysLabel.setText("Select Days:");
		bhStartTimeLabel = new JLabel();
		bhStartTimeLabel.setText("Business Start time:" );
		bhEndTimeLabel = new JLabel();
		bhEndTimeLabel.setText("Business End time:");
		//exsistingStartingTimeLabel = new JLabel();
		//exsistingStartingTimeLabel.setText("Starting time to locate slot:");
		
		bhStartTimeField = new JTextField(); 
		bhEndTimeField = new JTextField();
		//exsistingStartingTimeField = new JTextField();
		
		days = new HashMap<Integer, String>();
		bhSetWeekDaysBox = new JComboBox<String>();
		
		bhSetButton = new JButton();
		bhSetButton.setText("Set Business Hour");
		
		bhUpdateButton = new JButton();
		bhUpdateButton.setText("Update Business Hour");
		
		bhUpdateOldStartTimeField = new JTextField();
		bhUpdateNewStartTimeField = new JTextField();
		bhUpdateNewEndTimeField = new JTextField();
		
		bhSetWeekDaysBox.addItem("Monday");
		bhSetWeekDaysBox.addItem("Tuesday");
		bhSetWeekDaysBox.addItem("Wednesday");
		bhSetWeekDaysBox.addItem("Thursday");
		bhSetWeekDaysBox.addItem("Friday");
		bhSetWeekDaysBox.addItem("Saturday");
		bhSetWeekDaysBox.addItem("Sunday");
		
		//Update Garage Opening Hour
		garageOHSelectedLabel=new JLabel();
		garageOHRemoveButton=new JButton();
		garageOHAddButton=new JButton();
		garageOHSelectedLabel.setText("Garage: ");
		garageOHRemoveButton.setText("Delete Opening Hour");
		garageOHAddButton.setText("Add Opening Hour");
		garageOHStartTimeLabel=new JLabel();
		garageOHStartTimeLabel.setText("Garage Start Time:");
		garageOHEndTimeLabel=new JLabel();
		garageOHEndTimeLabel.setText("Garage End Time:");
		garageOHStartTimeField = new JTextField();
		garageOHEndTimeField = new JTextField();
		
		forOHselectedgarages = new HashMap<Integer, String>();
		garageOHSelectedBox = new JComboBox<String>();
		garageOHSelectedBox.addItem("Tire");
		garageOHSelectedBox.addItem("Engine");
		garageOHSelectedBox.addItem("Transmission");
		garageOHSelectedBox.addItem("Electronics");
		garageOHSelectedBox.addItem("Fluids");
		
		bhUpdateOldStartTimeLabel=new JLabel();
		bhUpdateOldStartTimeLabel.setText("Existing Starting time:");
		bhUpdateOldDateLabel = new JLabel();
		bhUpdateOldDateLabel.setText("Date to be changed:");
		bhUpdateNewDateLabel = new JLabel();
		bhUpdateNewDateLabel.setText("To target Date:");
		bhUpdateNewStartTimeLabel = new JLabel();
		bhUpdateNewStartTimeLabel.setText("To Start time(hh:mm):" );
		bhUpdateNewEndTimeLabel = new JLabel();
		bhUpdateNewEndTimeLabel.setText("To End time(hh:mm):" );
		
		bhUpdateButton = new JButton();
		bhUpdateButton.setText("Update Business Hour");
		

		updateDays = new HashMap<Integer, String>();
		bhUpdateOldWeekDaysBox = new JComboBox<String>();
		
		updateDays.put(0, "Monday");		bhUpdateOldWeekDaysBox.addItem("Monday");
		updateDays.put(0, "Tuesday");		bhUpdateOldWeekDaysBox.addItem("Tuesday");
		updateDays.put(0, "Wednesday ");	bhUpdateOldWeekDaysBox.addItem("Wednesday");
		updateDays.put(0, "Thursday ");		bhUpdateOldWeekDaysBox.addItem("Thursday");
		updateDays.put(0, "Friday ");		bhUpdateOldWeekDaysBox.addItem("Friday");
		updateDays.put(0, "Saturday");		bhUpdateOldWeekDaysBox.addItem("Saturday");
		updateDays.put(0, "Sunday ");		bhUpdateOldWeekDaysBox.addItem("Sunday");
		
		targetDays = new HashMap<Integer, String>();
		bhUpdateNewWeekDaysBoxDaysBox = new JComboBox<String>();
		
		targetDays.put(0, "Monday");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Monday");
		targetDays.put(0, "Tuesday");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Tuesday");
		targetDays.put(0, "Wednesday ");	bhUpdateNewWeekDaysBoxDaysBox.addItem("Wednesday");
		targetDays.put(0, "Thursday ");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Thursday");
		targetDays.put(0, "Friday ");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Friday");
		targetDays.put(0, "Saturday");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Saturday");
		targetDays.put(0, "Sunday ");		bhUpdateNewWeekDaysBoxDaysBox.addItem("Sunday");
		
		//Add service / Update service------------------------------------------------------------------------------------------------------------------
		addServiceTitle=new JLabel();
		updateServiceTitle=new JLabel();
		updateServiceNameLabel = new JLabel();
		addServiceTitle.setText("Add New Service");
		updateServiceTitle.setText("Update Existing Service");
		updateServiceNameLabel.setText("Selected Existing Service:");
		serviceNameLabel = new JLabel();
		serviceDurationLabel = new JLabel();
		garageSelectedLabel = new JLabel();
		serviceNameField = new JTextField();
		serviceDurationField = new JTextField();
		addServiceButton = new JButton();
		
		targetGarages = new HashMap<Integer, String>();
		garageSeletedComboBox = new JComboBox<String>();
		targetGarages.put(0, "Tire");		garageSeletedComboBox.addItem("Tire");
		targetGarages.put(0, "Engine");		garageSeletedComboBox.addItem("Engine");
		targetGarages.put(0, "Transmission");	garageSeletedComboBox.addItem("Transmission");
		targetGarages.put(0, "Electronics");		garageSeletedComboBox.addItem("Electronics");
		targetGarages.put(0, "Fluids ");		garageSeletedComboBox.addItem("Fluids");
		
		serviceNameLabel.setText("Service Name:");
		serviceDurationLabel.setText("Duration(Min):");
		garageSelectedLabel.setText("Garage:");
		addServiceButton.setText("Add New Service");
		
		serviceUpdateSelectedComboBox= new JComboBox<String>();
		serviceSelectedLabel = new JLabel();
		newNameLabel = new JLabel();
		newDurationLabel = new JLabel();
		newGarageLabel = new JLabel();
		
		serviceSelectedLabel.setText("Service to be Updated");
		newNameLabel.setText("New Name:");
		newDurationLabel.setText("New Duration(Min):");
		newGarageLabel.setText("New Garage:");
				
		newServiceNameField = new JTextField();
		newServiceDurationField = new JTextField();
			
		updateGarages = new HashMap<Integer, String>();
		newGarageSeletedComboBox = new JComboBox<String>();
		updateGarages.put(0, "Tire");		newGarageSeletedComboBox.addItem("Tire");
		updateGarages.put(0, "Engine");		newGarageSeletedComboBox.addItem("Engine");
		updateGarages.put(0, "Transmission ");	newGarageSeletedComboBox.addItem("Transmission");
		updateGarages.put(0, "Electronics ");		newGarageSeletedComboBox.addItem("Electronics");
		updateGarages.put(0, "Fluids ");		newGarageSeletedComboBox.addItem("Fluids");
		
		updateServiceButton = new JButton();
		updateServiceButton.setText("Update");
		//Make Appointment------------------------------------------------------------------------------------------------------------------
		makeAppTitle = new JLabel();
		makeAppTitle.setText("Make Appointment");
		appMakeDateLabel = new JLabel();
		appStartTimeLabel = new JLabel();
		appServiceSelectedLabel = new JLabel();
		appMakeDateLabel.setText("Date(yyyy-mm-dd):");
		appStartTimeLabel.setText("Start Time(hh:mm):");
		appServiceSelectedLabel.setText("Service:");
		appYear = new JTextField();
		appMonth = new JTextField();
		appDay = new JTextField();
		appStartTimeField = new JTextField();
		appServiceSelectedBox = new JComboBox<String>();
		appMakeButton = new JButton();
		appMakeButton.setText("Make Appointment");
		//Cancel Appointment------------------------------------------------------------------------------------------------------------------
		cancelAppTitle = new JLabel();
		cancelAppTitle.setText("Cancel Appointment");
		appCancelDateLabel = new JLabel();
		appCancelStartTimeLabel = new JLabel();
		appCancelServiceSelectedLabel = new JLabel();
		appCancelDateLabel.setText("Date(yyyy-mm-dd):");
		appCancelStartTimeLabel.setText("Start Time(hh:mm):");
		appCancelServiceSelectedLabel.setText("Service:");
		appCancelYear = new JTextField();
		appCancelMonth = new JTextField();
		appCancelDay = new JTextField();
		appCancelStartTimeField = new JTextField();
		appCancelServiceSelectedBox = new JComboBox<String>();
		appCancelButton = new JButton();
		appCancelButton.setText("Cancel Appointment");
		//Update Appointment
		updateAppTitle= new JLabel();
		updateAppTitle.setText("Update Appointment");
		appUpdateNameLabel= new JLabel();
		appUpdateServiceNameLabel= new JLabel();
		appUpdateNewDateLabel= new JLabel();
		appUpdateNewTimeLabel= new JLabel();
		appUpdateNameLabel.setText("Select an appointment to update:");
		appUpdateServiceNameLabel.setText("New Service");
		appUpdateNewDateLabel.setText("New Date(yyyy-mm-dd):");
		appUpdateNewTimeLabel.setText("New Time(hh:mm)");
		appUpdateServiceSelectedBox = new JComboBox<String>();
		appSelectedBox = new JComboBox<String>();
		appUpdateYear= new JTextField();
		appUpdateMonth= new JTextField();;
		appUpdateDay= new JTextField();;
		appUpdateTimeField= new JTextField();
		appUpdateButton= new JButton();
		appUpdateButton.setText("Update Appointment");
		//App Operation: Start/End Not Show
		appOpTitle = new JLabel();;
		appOpSelectedLabel = new JLabel();;
		appStartButton = new JButton();;
		appEndButton = new JButton();;
		appNotShowButton = new JButton();;
		appOpSelectedBox = new JComboBox<String>();
		
		appOpTitle.setText("Appointment Operation (For Owner)");
		appOpSelectedLabel.setText("Appointment:");
		appStartButton.setText("Start");;
		appEndButton.setText("End");;
		appNotShowButton.setText("Not Show");
		
		
		appViewTable = new JTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (!c.getBackground().equals(getSelectionBackground())) {
					Object obj = getModel().getValueAt(row, column);
					if (obj instanceof java.lang.String) {
						String str = (String)obj;
						c.setBackground(str.endsWith("sick)") ? Color.RED : str.endsWith("repair)") ? Color.YELLOW : Color.WHITE);
					}
					else {
						c.setBackground(Color.WHITE);
					}
				}
				return c;
			}
		};
		appViewScrollPane = new JScrollPane(appViewTable);
		this.add(appViewScrollPane);
		Dimension d = appViewTable.getPreferredSize();
		appViewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
		appViewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		appViewDateLabel=new JLabel();
		appViewDateLabel.setText("Select a date to view appointments:");
		appViewYear=new JTextField();
		appViewMonth=new JTextField();
		appViewDay=new JTextField();
		appViewSearchByDateButton=new JButton();
		appViewSearchByDateButton.setText("View");
		//Separator ------------------------------------------------------------------------------------------------------------------
		JSeparator horizontalLineTop = new JSeparator();
		JSeparator horizontalLineMiddle1 = new JSeparator();
		JSeparator horizontalLineMiddle2 = new JSeparator();
		JSeparator horizontalLineMiddle2_2 = new JSeparator();
		JSeparator horizontalLineMiddle3 = new JSeparator();
		JSeparator horizontalLineMiddle4 = new JSeparator();
		JSeparator horizontalLineMiddle5 = new JSeparator();
		JSeparator horizontalLineBottom = new JSeparator();
		//Layout------------------------------------------------------------------------------------------------------------------
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addComponent(horizontalLineTop)
				.addComponent(horizontalLineMiddle1)
				.addComponent(horizontalLineMiddle2)
				.addComponent(horizontalLineMiddle2_2)
				.addComponent(horizontalLineMiddle3)
				.addComponent(horizontalLineMiddle4)
				.addComponent(horizontalLineMiddle5)
				.addComponent(horizontalLineBottom)
				.addComponent(appViewScrollPane)
				
				//Step 3: update horizontal layout (add overviewScrollPane)

				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()

								.addComponent(userTitle)
								.addComponent(usernameLabel)
								.addComponent(biTitle)
								.addComponent(biEmailLabel)
								.addComponent(bhTitle)
								.addComponent(bhWeekDaysLabel)
								.addComponent(garageOHSelectedLabel)
								.addComponent(addServiceTitle)
								.addComponent(serviceNameLabel)
								.addComponent(updateServiceTitle)

								.addComponent(newNameLabel)
//								.addComponent(serviceUpdateSelectedComboBox)
								.addComponent(makeAppTitle)
								.addComponent(appMakeDateLabel)
								.addComponent(appStartTimeLabel)
								.addComponent(appServiceSelectedLabel)
								.addComponent(appMakeButton)
								.addComponent(appOpTitle)
								.addComponent(appOpSelectedLabel)
								.addComponent(appViewDateLabel)

								//Step 3: update horizontal layout (add driverLabel)
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(currentUserLabel)
								.addComponent(usernameTextField,100,100,150)
								.addComponent(biEmailField,100,100,150)
								.addComponent(bhSetWeekDaysBox)
								.addComponent(bhSetButton)
								.addComponent(garageOHSelectedBox)
								.addComponent(serviceNameField)
								.addComponent(newServiceNameField)
								.addComponent(appYear,70,70,70)
								.addComponent(appStartTimeField)
								.addComponent(appServiceSelectedBox)
								.addComponent(appOpSelectedBox)
								.addComponent(appViewYear)

								)
						.addGroup(layout.createParallelGroup()
								.addComponent(currentUserDisplayLabel)
								.addComponent(passwordLabel)
								.addComponent(biPhoneLabel)
								.addComponent(bhStartTimeLabel)
								.addComponent(garageOHStartTimeLabel)
//								.addComponent(updateServiceNameLabel)0
								.addComponent(serviceDurationLabel)
								.addComponent(updateServiceNameLabel)
								.addComponent(newDurationLabel)
//								.addComponent(newNameLabel)
								//.addComponent(newServiceNameField)
								.addComponent(appMonth,70,70,70)
								.addComponent(appStartButton)
								.addComponent(appViewMonth)

								
								// Step 3: update horizontal layout (add assignmentLabel)
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(passwordTextField,100,100,150)
								.addComponent(biPhoneField,100,100,150)
								.addComponent(bhStartTimeField,100,100,150)
								.addComponent(garageOHStartTimeField)
								.addComponent(garageOHAddButton)
								.addComponent(serviceUpdateSelectedComboBox)
								.addComponent(serviceDurationField)
								.addComponent(newServiceDurationField)
//								.addComponent(newServiceNameField)0
								.addComponent(appDay,70,70,70)
								.addComponent(appEndButton)
								.addComponent(appViewDay)

								)
						.addGroup(layout.createParallelGroup()
								.addComponent(currentTimeLabel)

								.addComponent(loginButton)
								.addComponent(biAddressLabel)
								.addComponent(bhEndTimeLabel)
								.addComponent(garageOHEndTimeLabel)
								.addComponent(garageSelectedLabel)
								.addComponent(newGarageLabel)
//								.addComponent(newDurationLabel)0
								.addComponent(cancelAppTitle)
								.addComponent(appCancelDateLabel)
								.addComponent(appCancelStartTimeLabel)
								.addComponent(appCancelServiceSelectedLabel)
								.addComponent(appCancelButton)
								.addComponent(appNotShowButton)
								.addComponent(appViewSearchByDateButton)

								)
						.addGroup(layout.createParallelGroup()
								.addComponent(currentTimeField)

								.addComponent(signUpButton)
								.addComponent(biAddressField)
								.addComponent(bhEndTimeField,100,100,150)
								.addComponent(garageOHEndTimeField)
								.addComponent(garageOHRemoveButton)
								//.addComponent(garageOHAddButton)
								.addComponent(garageSeletedComboBox)
								.addComponent(newGarageSeletedComboBox)
//								.addComponent(newServiceDurationField)0
								.addComponent(appCancelYear,70,70,70)
								.addComponent(appCancelStartTimeField)
								.addComponent(appCancelServiceSelectedBox)
								
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(currentTimeSetButton)
								.addComponent(logoutButton)
								.addComponent(biCarShopNameLabel)
								//.addComponent(bhSetButton)	
								//.addComponent(garageOHRemoveButton)
								.addComponent(addServiceButton)
								.addComponent(updateServiceButton)
								.addComponent(appCancelMonth,70,70,70)
								
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(updateAccountButton)
								.addComponent(biCarShopNameField,100,100,150)
								.addComponent(bhUpdateTitle)
								.addComponent(bhUpdateOldDateLabel)
								.addComponent(bhUpdateNewDateLabel)

								//.addComponent(exsistingStartingTimeLabel)	
//								.addComponent(newGarageLabel)1
								.addComponent(appCancelDay,70,70,70)
								
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(biUpdateButton)
								.addComponent(bhUpdateOldWeekDaysBox)
								.addComponent(bhUpdateNewWeekDaysBoxDaysBox)
								.addComponent(bhUpdateButton)
								.addComponent(appUpdateButton)
//								.addComponent(exsistingStartingTimeField)
//								.addComponent(bhUpdateNewStartTimeField)
								//.addComponent(exsistingStartingTimeField)
//								.addComponent(newGarageSeletedComboBox)1
//								.addComponent(updateAppTitle)
								.addComponent(appUpdateNameLabel)
								.addComponent(appUpdateServiceNameLabel)
								.addComponent(appUpdateNewDateLabel)
								.addComponent(appUpdateNewTimeLabel)
								
								
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(bhUpdateOldStartTimeLabel)
								.addComponent(bhUpdateNewStartTimeLabel)
								.addComponent(bhUpdateNewEndTimeLabel)

//								.addComponent(updateServiceButton)1
								.addComponent(appSelectedBox)
								.addComponent(appUpdateServiceSelectedBox)
								.addComponent(appUpdateYear,70,70,70)
								.addComponent(appUpdateTimeField)
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(bhUpdateOldStartTimeField)
								.addComponent(bhUpdateNewStartTimeField)
								.addComponent(bhUpdateNewEndTimeField)
								
								
								.addComponent(appUpdateMonth,70,70,70)
								)
						.addGroup(layout.createParallelGroup()
								.addComponent(appUpdateDay,70,70,70)

								)
						.addGroup(layout.createParallelGroup()
								.addComponent(bhUpdateNewEndTimeField)
								)
						)
						
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(currentTimeLabel)
						.addComponent(currentTimeField,25,25,25)
						.addComponent(currentTimeSetButton))
				
				.addGroup(layout.createParallelGroup()
						.addComponent(userTitle)
						.addComponent(currentUserLabel)
						.addComponent(currentUserDisplayLabel))
			
				.addGroup(layout.createParallelGroup()
						.addComponent(usernameLabel)
						.addComponent(passwordLabel)
						.addComponent(usernameTextField,25,25,25)
						.addComponent(passwordTextField,25,25,25)
						
						.addComponent(loginButton)
						.addComponent(signUpButton)
						.addComponent(logoutButton)
						.addComponent(updateAccountButton)
						)
						
				
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineTop))
				.addComponent(biTitle)
				.addGroup(layout.createParallelGroup()
						.addComponent(biEmailLabel)
						.addComponent(biEmailField,25,25,25)
						.addComponent(biPhoneLabel)
						.addComponent(biPhoneField,25,25,25)
						.addComponent(biAddressLabel)
						.addComponent(biAddressField,25,25,25)
						.addComponent(biCarShopNameLabel)
						.addComponent(biCarShopNameField,25,25,25)
						.addComponent(biUpdateButton)

							)
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle1))
				.addGroup(layout.createParallelGroup()
						.addComponent(bhTitle)
						.addComponent(bhUpdateTitle))

				.addGroup(layout.createParallelGroup()
						.addComponent(bhWeekDaysLabel)
						.addComponent(bhSetWeekDaysBox,25,25,25)
						.addComponent(bhStartTimeLabel)
						.addComponent(bhStartTimeField,25,25,25)
						.addComponent(bhEndTimeLabel)
						.addComponent(bhEndTimeField,25,25,25)
						//.addComponent(bhSetButton)
						.addComponent(bhUpdateOldDateLabel)
						.addComponent(bhUpdateOldWeekDaysBox)
						.addComponent(bhUpdateOldStartTimeLabel)
						.addComponent(bhUpdateOldStartTimeField)
	
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(garageOHSelectedLabel)
						.addComponent(garageOHSelectedBox,25,25,25)
						.addComponent(garageOHStartTimeLabel)
						.addComponent(garageOHStartTimeField)
						.addComponent(garageOHEndTimeLabel)
						.addComponent(garageOHEndTimeField)
						//.addComponent(garageOHAddButton)
						//.addComponent(garageOHRemoveButton)
						.addComponent(bhUpdateNewDateLabel)
						.addComponent(bhUpdateNewWeekDaysBoxDaysBox)
						.addComponent(bhUpdateNewStartTimeLabel)
						.addComponent(bhUpdateNewStartTimeField))
				.addGroup(layout.createParallelGroup()
						.addComponent(bhSetButton)
						.addComponent(garageOHAddButton)
						.addComponent(garageOHRemoveButton)
						.addComponent(bhUpdateButton)
						.addComponent(bhUpdateNewEndTimeLabel)
						.addComponent(bhUpdateNewEndTimeField))

				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle2))
				.addGroup(layout.createParallelGroup()
						.addComponent(addServiceTitle))
				.addGroup(layout.createParallelGroup()
						.addComponent(serviceNameLabel)
						.addComponent(serviceNameField,25,25,25)
						.addComponent(serviceDurationLabel)
						.addComponent(serviceDurationField,25,25,25)
						.addComponent(garageSelectedLabel)
						.addComponent(garageSeletedComboBox,25,25,25)
						.addComponent(addServiceButton)
						
						)
				.addComponent(horizontalLineMiddle2_2)
				.addGroup(layout.createParallelGroup()
						.addComponent(updateServiceTitle)
						.addComponent(updateServiceNameLabel)
						.addComponent(serviceUpdateSelectedComboBox))
				
				.addGroup(layout.createParallelGroup()
						.addComponent(serviceUpdateSelectedComboBox,25,25,25)
						.addComponent(newNameLabel)
						.addComponent(newServiceNameField,25,25,25)
						.addComponent(newDurationLabel)
						.addComponent(newServiceDurationField,25,25,25)

						.addComponent(newGarageLabel)
						.addComponent(newGarageSeletedComboBox,25,25,25)
						.addComponent(updateServiceButton)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle3))
				.addGroup(layout.createParallelGroup()
						.addComponent(makeAppTitle)
						.addComponent(cancelAppTitle)
						//.addComponent(updateAppTitle)
						.addComponent(appUpdateNameLabel)
						.addComponent(appSelectedBox,25,25,25)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(appMakeDateLabel)
						.addComponent(appYear,25,25,25)
						.addComponent(appMonth,25,25,25)
						.addComponent(appDay,25,25,25)
						.addComponent(appCancelDateLabel)
						.addComponent(appCancelYear,25,25,25)
						.addComponent(appCancelMonth,25,25,25)
						.addComponent(appCancelDay,25,25,25)
						.addComponent(appUpdateNewDateLabel)
						.addComponent(appUpdateYear,25,25,25)
						.addComponent(appUpdateMonth,25,25,25)
						.addComponent(appUpdateDay,25,25,25)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(appStartTimeLabel)
						.addComponent(appStartTimeField,25,25,25)
						.addComponent(appCancelStartTimeLabel)
						.addComponent(appCancelStartTimeField,25,25,25)
						.addComponent(appUpdateNewTimeLabel)
						.addComponent(appUpdateTimeField,25,25,25)


						)
				.addGroup(layout.createParallelGroup()
						.addComponent(appServiceSelectedLabel)
						.addComponent(appServiceSelectedBox,25,25,25)
						.addComponent(appCancelServiceSelectedLabel)
						.addComponent(appCancelServiceSelectedBox,25,25,25)
						.addComponent(appUpdateServiceNameLabel)
						.addComponent(appUpdateServiceSelectedBox,25,25,25)
//						.addComponent(appUpdateNewDateLabel)
//						.addComponent(appUpdateYear,25,25,25)
//						.addComponent(appUpdateMonth,25,25,25)
//						.addComponent(appUpdateDay,25,25,25)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(appMakeButton)
						.addComponent(appCancelButton)
						.addComponent(appUpdateButton)
						)
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle4))
				.addComponent(appOpTitle)
				.addGroup(layout.createParallelGroup()
						.addComponent(appOpSelectedLabel)
						.addComponent(appOpSelectedBox,25,25,25)
						.addComponent(appStartButton)
						.addComponent(appEndButton)
						.addComponent(appNotShowButton))
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle5))
				.addGroup(layout.createParallelGroup()
						.addComponent(appViewDateLabel)
						.addComponent(appViewYear,25,25,25)
						.addComponent(appViewMonth,25,25,25)
						.addComponent(appViewDay,25,25,25)
						.addComponent(appViewSearchByDateButton))
				//.addComponent(newNameLabel)
				.addGroup(layout.createParallelGroup()
						.addComponent(appViewScrollPane))
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineBottom))
	
				);

		// --------------------------------------- listeners ---------------------------------------
				//listener for setting date and time
				currentTimeSetButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						currentTimeSetButtonActionPerformed(evt);
					}
				});
				// listener for sign up
				signUpButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						signUpButtonActionPerformed(evt);
					}
				});
				
				// listener for login
				loginButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						loginButtonActionPerformed(evt);
					}
				});

				// listener for logout
				logoutButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						logOutButtonActionPerformed(evt);
					}
				});
				
				// listener for update account
				updateAccountButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateAccountButtonActionPerformed(evt);
					}
				});
				
				// listener for update business information 
				biUpdateButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						biUpdateButtonActionPerformed(evt);
					}
				});
				
				// listener for update business hour 
				bhUpdateButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						bhUpdateButtonActionPerformed(evt);
					}
				});
				
				// listener for set business hour
				bhSetButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						bhSetButtonActionPerformed(evt);
					}
				});
				
				// listener for add garage opening hour
				garageOHAddButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						garageOHAddButtonActionPerformed(evt);
					}
				});
				
				// listener for remove garage opening hour
				garageOHRemoveButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						garageOHRemoveButtonActionPerformed(evt);
					}
				});
				
				//listener for ComboBox of Business Hour/Garage OH 's WEEKDAYS
				bhSetWeekDaysBox.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						bhSetWeekDaysBoxActionPerformed(evt);
					}
				});
				//listener for ComboBox of Garage (Section of BH/GarageOH)
				garageOHSelectedBox.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						garageOHSelectedBoxActionPerformed(evt);
					}
				});
				
				// listener for add service
				addServiceButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addServiceButtonActionPerformed(evt);
					}
				});

				// listener for update service
				updateServiceButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateServiceButtonActionPerformed(evt);
					}
				});
				
				// listener for make Appointment
				appMakeButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						makeAppointmentButtonActionPerformed(evt);
					}
				});
				
				// listener for update Appointment
				appUpdateButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateAppointmentButtonActionPerformed(evt);
					}
				});
				
				// listener for	cancel Appointment
				appCancelButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						cancelAppointmentButtonActionPerformed(evt);
					}
				});

				// listener for No Show
				appNotShowButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						noShowButtonActionPerformed(evt);
					}
				});
				// listener for start app
				appStartButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						startAppButtonActionPerformed(evt);
					}
				});
				// listener for end app
				appEndButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						endAppButtonActionPerformed(evt);
					}
				});
				appViewSearchByDateButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						appViewSearchByDateButtonActionPerformed(evt);
					}
				});
				refreshDailyOverview();
		pack();
	}
	
	public void currentTimeSetButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("currentTimeSetButtonActionPerformed");
		error = "";
		success = "";
		
		try {
				String input=this.currentTimeField.getText();
				CarShopController.setTime(input);
		} catch (Exception e) {
			refreshData();
			error="Fail to Set Date and Time, please check format";
		}
		
		if(error == "") {
			success = "set current time successfully";
		}

		
		// update view
		refreshData();
	}
	/**
	 * When sign up button is clicked, the system tries to let the customer sign up with the filled information in the username and password text field
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void signUpButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("signUpButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.signUp(usernameTextField.getText(),passwordTextField.getText());
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			try {
				CarShopController.logIn(usernameTextField.getText(),passwordTextField.getText());
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
			if(error == "") {
				success = "sign up successfully! Welcome!";
			}
		}
		
		// update view
		refreshData();
	}
	
	/**
	 * When login button is clicked, the system tries to let the customer login with the filled information in the username and password text field
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("loginButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.logIn(usernameTextField.getText(),passwordTextField.getText());
			this.appViewDay.setText("");
			this.appViewMonth.setText("");
			this.appViewYear.setText("");
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "login successfully";
		}
				
		// update view
		refreshData();
	}
	
	/**
	 * When logout button is clicked, the system tries to let the current login user to logout.
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("logOutButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.logOut();
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		// update view
		refreshData();
	}
	
	/**
	 * When update account button is clicked, the system tries to let the customer update his/her account with the filled information in the username and password text field
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void updateAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("updateAccountButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.updateAccount(usernameTextField.getText(),passwordTextField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "update account successfully";
		}
		
		// update view
		refreshData();
	}

	private void biUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("biUpdateButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			if(CarShopController.getCurrentTOBusiness()!=null) {
				CarShopController.updateBusinessInformation(biCarShopNameField.getText(), biAddressField.getText(), biPhoneField.getText(), biEmailField.getText());
			}else {
				CarShopController.setupBusinessInformation(biCarShopNameField.getText(), biAddressField.getText(), biPhoneField.getText(), biEmailField.getText());
			}
			
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "set business information successfully";
		}
		
		// update view
		refreshData();
	}
	
	private void bhUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("bhUpdateButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.updateExistingBusinessHours((String)bhUpdateOldWeekDaysBox.getSelectedItem(), bhUpdateOldStartTimeField.getText(), 
					(String)bhUpdateNewWeekDaysBoxDaysBox.getSelectedItem(), bhUpdateNewStartTimeField.getText(), bhUpdateNewEndTimeField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "update business hour successfully";
		}
		
		// update view
		refreshData();
		
	}
	
	private void bhSetButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("bhSetButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.addBusinessHour((String)bhSetWeekDaysBox.getSelectedItem(), bhStartTimeField.getText(), bhEndTimeField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "set business hour successfully";
		}
		
		// update view
		refreshData();

	}

	private void bhSetWeekDaysBoxActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("bhSetWeekDaysBoxActionPerformed");
		refreshData();
	}

	private void garageOHSelectedBoxActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("garageOHSelectedBoxActionPerformed");
		refreshData();
	}
	
	/**
	 * When the garage Opening hour add button is pressed, the user tries to add an opening hour to the selected garage in the garage list
	 * Only corresponding technician of the garage is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void garageOHAddButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("garageOHAddButtonActionPerformed");
		// clear error message
		error = "";
		success="";
				
		// call controller method
		try {
			CarShopController.addGarageOpeningHour(currentUserDisplayLabel.getText(), (String)bhSetWeekDaysBox.getSelectedItem(), garageOHStartTimeField.getText(), garageOHEndTimeField.getText(), (String)garageOHSelectedBox.getSelectedItem());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "add garage opening hour successfully";
		}
		
		// update view
		refreshData();		
	}
	
	/**
	 * When the garage Opening hour remove button is pressed, the user tries to remove an existing opening hour to the selected garage in the garage list
	 * Only corresponding technician of the garage is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void garageOHRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("garageOHRemoveButtonActionPerformed");
		// clear error message
		error = "";
		success="";
		
		// call controller method
		try {
			CarShopController.removeGarageOpeningHour(currentUserDisplayLabel.getText(), (String)bhSetWeekDaysBox.getSelectedItem(), this.garageOHStartTimeField.getText(), garageOHEndTimeField.getText(), (String)garageOHSelectedBox.getSelectedItem());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "remove garage opening hour successfully";
		}
		
		// update view
		refreshData();		
	}
	
	/**
	 * When add service button is clicked, the system tries to let the user add service to the car shop with the filled information in the service name text field, duration text field and garage selected item
	 * Only owner is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("addServiceButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.addService(serviceNameField.getText(),Integer.parseInt(serviceDurationField.getText()) , (String)(garageSeletedComboBox.getSelectedItem()));
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "add service successfully";
		}
		
		// update view
		refreshData();
	}
	
	/**
	 * When update service button is clicked, the system tries to let the user update the service to the car shop with the selected existing service item, the filled information in the new service name text field, new duration text field and new garage selected item
	 * Only owner is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void updateServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("updateServiceButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			CarShopController.updateService((String)(serviceUpdateSelectedComboBox.getSelectedItem()), newServiceNameField.getText(), Integer.parseInt(newServiceDurationField.getText()), (String)(newGarageSeletedComboBox.getSelectedItem()));
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "update service successfully";
		}
		
		// update view
		refreshData();
	}
	

	/**
	 * When make appointment button is clicked, the system tries to let the user make the appointment with the data in date text field, appointment start time text field and the selected item of appointment service list 
	 * Only customer is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void makeAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("makeAppointmentButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			String year = appYear.getText();
			String month = appMonth.getText();
			String day = appDay.getText();
			String date = year+"-"+month+"-"+day;
			CarShopController.makeAppointment(currentUserDisplayLabel.getText(), date, (String)(appServiceSelectedBox.getSelectedItem()), appStartTimeField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "make appointment successfully";
		}
		
		// update view
		refreshData();
	}
	
	/**
	 * When update appointment button is clicked, the system tries to let the user update the appointment with the selected appointment in the update appointment combo box, the data in new date text field, new appointment start time text field and the selected item of new appointment service list 
	 * Only customer is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void updateAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("updateAppointmentButtonActionPerformed");
		// clear error message
		error = "";
		success="";
		
		// call controller method
		try {
			String year = appUpdateYear.getText();
			String month = appUpdateMonth.getText();
			String day = appUpdateDay.getText();
			String date = year+"-"+month+"-"+day;
			Integer appNum = Integer.parseInt((String)appSelectedBox.getSelectedItem());
			TOAppointment selectedAppointment = toAppointmentMap.get(appNum);		
			CarShopController.updateServiceOfAppointmentUI(selectedAppointment, currentUserDisplayLabel.getText(), (String)appUpdateServiceSelectedBox.getSelectedItem(),date, appUpdateTimeField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "update appointment successfully";
		}
		
		// update view
		refreshData();
	}


	/**
	 * When cancle appointment button is clicked, the system tries to let the user cancle the appointment with the data in cancel date text field, cancel appointment start time text field and the selected item of cancel appointment service list 
	 * Only customer is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void cancelAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("cancelAppointmentButtonActionPerformed");
		// clear error message
		error = "";
		success = "";
		
		// call controller method
		try {
			String year = appCancelYear.getText();
			String month = appCancelMonth.getText();
			String day = appCancelDay.getText();
			String date = year+"-"+month+"-"+day;
			CarShopController.cancelAppointment(currentUserDisplayLabel.getText(), currentUserDisplayLabel.getText(), date, (String)appCancelServiceSelectedBox.getSelectedItem(), appCancelStartTimeField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "cancel appointment successfully";
		}
		
		// update view
		refreshData();
	}


	/**
	 * When the not show button is pressed, the user tries to add one more no show record on the customer of the selected appointment 
	 * Only owner is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */
	private void noShowButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("noShowButtonActionPerformed");
		// clear error message
		error = "";
		success="";
		
		// call controller method
		try {
			Integer appNum = Integer.parseInt((String)appOpSelectedBox.getSelectedItem());
			TOAppointment selectedAppointment = toAppointmentMap.get(appNum);	
			CarShopController.notShowUpUI(selectedAppointment);
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "record one more not show account successfully";
		}
		
		// update view
		refreshData();
	}
	


	/**
	 * When the start appointment button is pressed, the user tries to start the selected appointment.
	 * Only owner is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */	
	private void startAppButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("startAppButtonActionPerformed");
		// clear error message
		error = "";
		success="";
		
		// call controller method
		try {
			Integer appNum = Integer.parseInt((String)appOpSelectedBox.getSelectedItem());
			TOAppointment selectedAppointment = toAppointmentMap.get(appNum);
			CarShopController.ownerStartAppUI(selectedAppointment);
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "start appointment successfully";
		}
		
		// update view
		refreshData();
	}
	

	/**
	 * When the start appointment button is pressed, the user tries to start the selected appointment.
	 * Only owner is allowed to perform this operation. 
	 * @author Shichang Zhang
	 * @param evt the click event
	 */		
	private void endAppButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("endAppButtonActionPerformed");
		// clear error message
		error = "";
		success="";
		
		// call controller method
		try {
			Integer appNum = Integer.parseInt((String)appOpSelectedBox.getSelectedItem());
			TOAppointment selectedAppointment = toAppointmentMap.get(appNum);		
			CarShopController.ownerEndAppNormallyUI(selectedAppointment);
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		if(error == "") {
			success = "end appointment successfully";
		}
		
		// update view
		refreshData();
	}
	/**
	 * When the view appointment by date button, the user is able to view the appointment on that date.
	 * Only owner is allowed to perform this operation. 
	 * @author Junjian Chen
	 * @param evt evt the click event
	 */
	private void appViewSearchByDateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("appViewSearchByDateButtonActionPerformed");
		error="";
		//Date initDate=Date.valueOf("2000-1-1");
		try {

			refreshDailyOverview();	
		}catch (Exception e) {
			error = e.getMessage();
		}
		refreshData();
	}
	
	/**
	 * When a control button is pressed, the UI interface is expected to update. 
	 * The function reads the data in the system and show the data on the UI interface. 
	 */
	private void refreshData() {
		toAppointmentMap = new HashMap<Integer, TOAppointment>();
		for(TOAppointment toA : CarShopController.getServiceAppointments()) {
			toAppointmentMap.put(toA.getAppNum(), toA);
		}
		
		refreshError();
		refreshUserInfo();
		refreshBusinessInfo();
		refreshGarageOH();
		refreshServices();
		refreshAppointment();
		refreshAppointmentOperation();
		refreshBusinessInfo();
		refreshBusinessOH();
		refreshCurrentTime();
		refreshDailyOverview();
		System.out.println(CarShopController.getStatus());
	}
	
	/**
	 * reload the error message
	 * @author Yuyan Shi
	 */
	private void refreshError() {
		if(success != "") {
			errorMessage.setForeground(Color.GREEN);
			errorMessage.setText("Message: "+success);
		}else if(error != "") {
			errorMessage.setForeground(Color.RED);
			errorMessage.setText("Error: "+error);
		}else {
			errorMessage.setForeground(Color.BLACK);
			errorMessage.setText("Message: ");
		}
		error = "";
		success = "";
	}

	/**
	 * reload current user
	 * @author Yuyan Shi,Junjian Chen
	 */
	private void refreshUserInfo() {
		if(CarShopController.getTOCurrentUser() != null) {
			this.usernameTextField.setText("");
			this.passwordTextField.setText("");
			this.currentUserDisplayLabel.setText(CarShopController.getTOCurrentUser().getUsername());
		} else {
			this.usernameTextField.setText("");
			this.passwordTextField.setText("");
			this.currentUserDisplayLabel.setText("");
		}
	}
	
	/**
	 * reload business info
	 * @author Yuyan Shi, John
	 */
	private void refreshBusinessInfo() {
		if(CarShopController.getCurrentTOBusiness()!=null) {
			this.biAddressField.setText(CarShopController.getCurrentTOBusiness().getAddress());
			this.biCarShopNameField.setText(CarShopController.getCurrentTOBusiness().getName());
			this.biEmailField.setText(CarShopController.getCurrentTOBusiness().getEmail());
			this.biPhoneField.setText(CarShopController.getCurrentTOBusiness().getPhoneNumber());
		}else {
			this.biAddressField.setText("");
			this.biCarShopNameField.setText("");
			this.biEmailField.setText("");
			this.biPhoneField.setText("");
		}
		
	}
	/**
	 * reload business Hour
	 * @author John
	 */
	private void refreshBusinessOH() {
		//set business hour
		String day=(String)this.bhSetWeekDaysBox.getSelectedItem();
		if(CarShopController.getTOBusinessHour(day)!=null) {
			this.bhStartTimeField.setText(CarShopController.getTOBusinessHour(day).getStartTime());
			this.bhEndTimeField.setText(CarShopController.getTOBusinessHour(day).getEndTime());
		}else {
			this.bhStartTimeField.setText("");
			this.bhEndTimeField.setText("");
		}
		
		//add business hour
		this.bhUpdateOldWeekDaysBox.setSelectedIndex(-1);
		this.bhUpdateOldStartTimeField.setText("");
		this.bhUpdateNewStartTimeField.setText("");
		this.bhUpdateNewEndTimeField.setText("");
		this.bhUpdateNewWeekDaysBoxDaysBox.setSelectedIndex(-1);
	}
	/**
	 * reload garage opening opening hour
	 * Reload garage start time and end time
	 */
	private void refreshGarageOH() {
		String day=(String)this.bhSetWeekDaysBox.getSelectedItem();
        String garageType=(String)this.garageOHSelectedBox.getSelectedItem(); 
        if(CarShopController.getTOGarageOH(day, garageType)!=null) {
        	this.garageOHStartTimeField.setText(CarShopController.getTOGarageOH(day, garageType).getStartTime());
        	this.garageOHEndTimeField.setText(CarShopController.getTOGarageOH(day, garageType).getEndTime());
        }else {
        	this.garageOHStartTimeField.setText("");
        	this.garageOHEndTimeField.setText("");
        }
	}
	
	/**
	 * reload service part
	 * @author Shichang Zhang
	 */
	private void refreshServices() {
		this.serviceNameField.setText("");
		this.serviceDurationField.setText("");
		this.garageSeletedComboBox.setSelectedIndex(-1);
		
		this.serviceUpdateSelectedComboBox.removeAllItems();
		ArrayList<TOService> toServices = CarShopController.getServices();
		for(TOService tos : toServices) {
			serviceUpdateSelectedComboBox.addItem(tos.getName());
		}
		this.serviceUpdateSelectedComboBox.setSelectedIndex(-1);
		
		this.newServiceNameField.setText("");
		this.newServiceDurationField.setText("");
		this.newGarageSeletedComboBox.setSelectedIndex(-1);
	}


	/**
	 * reload appointment part
	 */
	private void refreshAppointment() {		
		//make appointment
		this.appYear.setText("");
		this.appMonth.setText("");
		this.appDay.setText("");
		this.appStartTimeField.setText("");
		this.appServiceSelectedBox.removeAllItems();
		ArrayList<TOService> toServices = CarShopController.getServices();
		for(TOService tos : toServices) {
			appServiceSelectedBox.addItem(tos.getName());
		}
		this.appServiceSelectedBox.setSelectedIndex(-1);
		
		//cancel appointment
		this.appCancelYear.setText("");
		this.appCancelMonth.setText("");
		this.appCancelDay.setText("");
		this.appCancelStartTimeField.setText("");
		this.appCancelServiceSelectedBox.removeAllItems();
		for(TOService tos : toServices) {
			appCancelServiceSelectedBox.addItem(tos.getName());
		}
		this.appCancelServiceSelectedBox.setSelectedIndex(-1);
		
		//Update appointment
		this.appSelectedBox.removeAllItems();
		ArrayList<TOAppointment> toAppointments = CarShopController.getServiceAppointments();
		for(TOAppointment toA : toAppointments) {
			String s = toA.getAppNum()+"";
			if(toA.getCustomerName().equals(currentUserDisplayLabel.getText())) this.appSelectedBox.addItem(s);
		}
		this.appSelectedBox.setSelectedIndex(-1);		
		this.appUpdateYear.setText("");
		this.appUpdateMonth.setText("");
		this.appUpdateDay.setText("");
		this.appUpdateTimeField.setText("");
		this.appUpdateServiceSelectedBox.removeAllItems();
		for(TOService tos : toServices) {
			appUpdateServiceSelectedBox.addItem(tos.getName());
		}
		this.appUpdateServiceSelectedBox.setSelectedIndex(-1);
		
	}
	
	/**
	 * reload appointment part
	 */
	private void refreshAppointmentOperation() {
		this.appOpSelectedBox.removeAllItems();
		ArrayList<TOAppointment> toAppointments = CarShopController.getServiceAppointments();
		for(TOAppointment toA : toAppointments) {
			String s = toA.getAppNum()+"";
			this.appOpSelectedBox.addItem(s);
		}
		this.appOpSelectedBox.setSelectedIndex(-1);	
	}
	
	/**
	 * Refresh the data of table
	 * @author Junjian Chen
	 */
	private void refreshDailyOverview() {
		overviewDtm = new DefaultTableModel(0, 0);
		overviewDtm.setColumnIdentifiers(overviewColumnNames);
		appViewTable.setModel(overviewDtm);
		String year=appViewYear.getText();
		String month=appViewMonth.getText();
		String day=appViewDay.getText();
		if(year.length()==0||month.length()==0||day.length()==0) {
			return;
		}
		String dd = year+"-"+month+"-"+day;
		Date d = Date.valueOf(dd);
		for(TOAppointment item:CarShopController.getAppOverview(d)) {
			String customer = item.getCustomerName();
			String service=item.getBookableServiceName();
			String date=item.getStartDate();
			String startTime =item.getStartTime();
			String endTime = item.getEndTime();
			Object[] obj = {item.getAppNum(),customer,service,date,startTime,endTime};
			overviewDtm.addRow(obj);
		}
		Dimension di = appViewTable.getPreferredSize();
		appViewScrollPane.setPreferredSize(new Dimension(di.width, HEIGHT_OVERVIEW_TABLE));
	}
	
	private void refreshCurrentTime() {
		this.currentTimeField.setText(CarShopController.getCurrentTime());
	}
}