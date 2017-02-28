package netgloo.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import netgloo.dao.AreaDao;
import netgloo.dao.DinningTableDao;
import netgloo.dao.EmployeeDao;
import netgloo.dao.RestaurantDao;
import netgloo.dao.RestaurantManagerDao;
import netgloo.dao.UserDao;
import netgloo.models.Area;
import netgloo.models.AreaImmitation;
import netgloo.models.DiningTable;
import netgloo.models.Employee;
import netgloo.models.EmployeePom;
import netgloo.models.Provider;
import netgloo.models.Restaurant;
import netgloo.models.RestaurantManager;
import netgloo.models.RestaurantManagerPom;
import netgloo.models.SpaceXY;
import netgloo.models.SystemManager;
import netgloo.models.TablePrint;
import netgloo.models.User;
import netgloo.models.UserEmployeePom;
import netgloo.models.UserProba;

@RestController
@RequestMapping("/restaurantManagerController")
public class RestaurantManagerController {

	@Autowired
	private RestaurantManagerDao restManDao;

	@Autowired
	private RestaurantDao restDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private EmployeeDao empDao;
	
	@Autowired
	AreaDao aDao;
	
	@Autowired 
	DinningTableDao dtDao;

	ArrayList<RestaurantManager> listOfRestaurantManagers = new ArrayList<RestaurantManager>();

	@RequestMapping(value = "/createNewRestaurantManager", method = RequestMethod.POST, headers = {
			"content-type=application/json" })
	public String createRestaurantManager(@RequestBody RestaurantManagerPom rm1) {
		System.out.println("ispis iz managera --" + rm1.getRestaurantId());
		try {

			Restaurant restaurant = restDao.findByRestaurantId(Long.parseLong(rm1.getRestaurantId()));

			RestaurantManager restaurantManager = null;
			restaurantManager = new RestaurantManager(rm1.getRestaurantManagerNickId(), rm1.getRestaurantManagerMail(),
					rm1.getRestaurantManagerName(), rm1.getRestaurantManagerSurname(),
					rm1.getRestaurantManagerPassword(), restaurant);
			restManDao.save(restaurantManager);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "EXCEPTION";
		}
		return "OK";
	}

	@RequestMapping(value = "/loginRestaurantManager", method = RequestMethod.POST, headers = {
			"content-type=application/json" })
	public String loginRestaurantManager(@RequestBody RestaurantManager rm1, HttpServletRequest request) {
		try {
			RestaurantManager restaurantManager = restManDao.findOne(rm1.getRestaurantManagerNickId());
			if (restaurantManager != null) {
				String nick = String.valueOf(restaurantManager.getRestaurantManagerNickId());
				String pass = String.valueOf(restaurantManager.getRestaurantManagerPassword());
				if (nick.equals(rm1.getRestaurantManagerNickId()) && pass.equals(rm1.getRestaurantManagerPassword())) {
					request.getSession().setAttribute("restaurantManager", restaurantManager);
					return "restaurantManager";
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "EXCEPTION";
		}
		return "OK";
	}

	@RequestMapping(value = "/updateRestaurant", method = RequestMethod.POST, headers = {
			"content-type=application/json" })
	@ResponseBody
	public String updateRestaurant(@RequestBody Restaurant r1, RestaurantManager rm1, HttpServletRequest request) {
		try {
			/*
			 * if (restDao.findByRestaurantId(r1.getRestaurantId()) ==
			 * rm1.getRestaurantId()) { System.out.println(
			 * "ID menadzera i restorana JESU isti"); if
			 * (restDao.findByRestaurantId(r1.getRestaurantId()) != null) {
			 */

			RestaurantManager rm = (RestaurantManager) request.getSession().getAttribute("restaurantManager");

			if (rm != null) {
				if (rm.getRestaurantId() != null) {
					Restaurant restaurant = rm.getRestaurantId();
					// Restaurant restaurant =
					// restDao.findByRestaurantId(r1.getRestaurantId());
					restaurant.setRestaurantName(r1.getRestaurantName());
					restaurant.setRestaurantType(r1.getRestaurantType());
					restaurant.setRestaurantCoordinates(r1.getRestaurantCoordinates());
					restaurant.setRestaurantAdress(r1.getRestaurantAdress());
					restaurant.setRestaurantRate(r1.getRestaurantRate());
					restaurant.setRestaurantVisitsNumber(r1.getRestaurantVisitsNumber());
					restaurant.setRestaurantIncome(r1.getRestaurantIncome());
					restDao.save(restaurant);
				}
			}

			System.out.println("JE LI RAVNO OVO? JE LI RAVNO OVO?");

			// return "OK";
			/*
			 * } else { return "NOT_OK"; } }else{ System.out.println(
			 * "ID menadzera i restorana NISU isti"); }
			 */
		} catch (Exception ex) {
			return "Error updating restaurant: " + ex.toString();
		}
		// return null;
		return null;
	}

	@RequestMapping(value = "/restaurantData", method = RequestMethod.GET)
	public Restaurant restaurantData(HttpServletRequest request) {
		try {
			if (request.getSession().getAttribute("restaurantManager") != null) {
				System.out.println("PROCURILO U OVO ... sto je dobro");
				RestaurantManager rmkkk = (RestaurantManager) request.getSession().getAttribute("restaurantManager");
				Restaurant restaurant = rmkkk.getRestaurantId(); // (Restaurant)
																	// request.getSession().getAttribute("restaurantManager");
				return restaurant;
			}

		} catch (Exception e) {
			System.out.println("no restaurant data");
		}
		return null;
	}

	@RequestMapping(value = "/createNewEmployee", method = RequestMethod.POST, headers = {
			"content-type=application/json" })
	public String createEmployee(@RequestBody UserEmployeePom ue1, HttpServletRequest request) {

		try {

			User user = null;
			String user_reg_date = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
			user = new User(ue1.getEmail(), ue1.getPassword(), ue1.getName(), ue1.getSurname(),
					ue1.getBirthDate().trim(), user_reg_date, "Employee", false);
			userDao.save(user);

			RestaurantManager rmkkk = (RestaurantManager) request.getSession().getAttribute("restaurantManager");
			Restaurant restaurant = rmkkk.getRestaurantId();

			User userid = userDao.findByEmail(ue1.getEmail());

			Employee emp = null;
			emp = new Employee(userid, restaurant, ue1.getEmployeeRole(), ue1.getEmployeeConfectionNumber(),
					ue1.getEmployeeShoeSize(), ue1.getEmployeeRate(), false, 0, 0);
			empDao.save(emp);

		} catch (Exception ex) {
			ex.printStackTrace();
			return "EXCEPTION";
		}
		return "OK";
	}
	
	

	@RequestMapping(value = "/logoutRestaurantManager", method = RequestMethod.GET, headers = {
			"content-type=application/json" })
	public String logoutRestaurantManager(HttpServletRequest request) {

		try {
			request.getSession().invalidate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "logout";
	}
	
	@RequestMapping(value = "/getAreasForRM", method = RequestMethod.POST)
	public ArrayList<AreaImmitation> getAreasForRM(HttpServletRequest request) {
		//friends.clear();
		
		 ArrayList<AreaImmitation> areasList = new ArrayList<AreaImmitation>();
		areasList.clear();
		
		try {

			RestaurantManager rManager = (RestaurantManager) request.getSession().getAttribute("restaurantManager");
			if(rManager!=null)
			{


				ArrayList<Area> al = new ArrayList<Area>();
				al = aDao.findAllByRestaurant(rManager.getRestaurantId());

				for(Area a : al)
				{
					areasList.add(new AreaImmitation(a.getAreaName(),a.getRestaurant().getRestaurantName(),a.getAreaID(),a.getRestaurant().getRestaurantId()));
				}

				for(AreaImmitation a : areasList)
				{
					System.out.println("AREA: " + a.getAreaName() +" , - r: " + a.getRestaurantName());
				}
			}		

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return areasList;
	}
	
	
	@RequestMapping(value = "/getAreaSpaceRM", method = RequestMethod.POST, headers = { "content-type=application/json" })
	public SpaceXY getAreaSpaceRM(@RequestBody AreaImmitation ai, HttpServletRequest request) {
		
		
		Area areax = aDao.findOne(ai.getAreaID());
		
		SpaceXY sxy = new SpaceXY(areax.getSpaceX(),areax.getSpaceY());
		
		return sxy;
	}
	
	
	@RequestMapping(value = "/removeTableRM", method = RequestMethod.POST, headers = { "content-type=application/json" })
	public String removeTableRM(@RequestBody TablePrint tabPrint, HttpServletRequest request) {
		
		
		//Area areax = aDao.findOne(ai.getAreaID());
		
		//SpaceXY sxy = new SpaceXY(areax.getSpaceX(),areax.getSpaceY());
		
		Long gtID = tabPrint.getGeneralTableID();
		
		DiningTable dt = dtDao.findByGeneralTableID(gtID);
		
		dt.setActive(false);
		
		dtDao.save(dt);
		
		
		return "super";
		
	}
	
	@RequestMapping(value = "/getAreaTablesRM", method = RequestMethod.POST, headers = { "content-type=application/json" })
	public ArrayList<TablePrint> getAreaTablesRM(@RequestBody AreaImmitation ai, HttpServletRequest request) {
		
		ArrayList<DiningTable> dtList = new ArrayList<DiningTable>();
		ArrayList<TablePrint> tp = new ArrayList<TablePrint>();
		System.out.println("USLO JE U OVO OVDE ZA TABLE");
		System.out.println("SELECTED AREA: "+ ai.areaID);
		
		Area areax = aDao.findOne(ai.getAreaID());
		
		dtList = dtDao.findAllByArea(areax);


		for(DiningTable dt: dtList)
		{

			if(dt.isActive()==true)
			{


				if(dt.getCurrentGuestsOrder()!=null)
				{
					if(dt.getCurrentGuestsOrder().getWaiter()!=null)
					{
						tp.add(new TablePrint(dt.getGeneralTableID(),dt.getOccupied(),dt.getPositionX(),dt.getPositionY(),areax.getSpaceX(),areax.getSpaceY(),dt.getTableNumberInRestaurant(),areax.getAreaID(),dt.getCurrentGuestsOrder().getWaiter().getEmployeeId(), dt.getCurrentGuestsOrder().getOrderID()));
					}
					else
					{
						tp.add(new TablePrint(dt.getGeneralTableID(),dt.getOccupied(),dt.getPositionX(),dt.getPositionY(),areax.getSpaceX(),areax.getSpaceY(),dt.getTableNumberInRestaurant(),areax.getAreaID(),(long) -1, dt.getCurrentGuestsOrder().getOrderID()));
					}
				}
				else
				{
					tp.add(new TablePrint(dt.getGeneralTableID(),dt.getOccupied(),dt.getPositionX(),dt.getPositionY(),areax.getSpaceX(),areax.getSpaceY(),dt.getTableNumberInRestaurant(),areax.getAreaID(),(long) -1, -1));

				}
			}
		}
		
		for(DiningTable dt: dtList)
		{
			System.out.println("Table: " + dt.getPositionX()+", "+dt.getPositionY()+ " -> area: "+dt.getArea());
		}
		
		return tp;
	}
	
	
	
	
	
}
