package mil.nga.bundler.statistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility class used to calculate time slices associated with individual
 * days.  
 *  
 * @author L. Craig Carpenter
 */
public class DateModelFactory {

	/** 
	 * Default constructor enforcing the singleton design pattern
	 */
	private DateModelFactory() {}
	
	/**
	 * Compare two Calendar objects to see if the fall on the same day.
	 * 
	 * @param oneCalendar A populated Calendar object
	 * @param anotherCalendar A populated Calendar object.
	 * @return True if the Calendars fall on the same day, false otherwise.
	 */
	private boolean isSameDay(
			Calendar oneCalendar, 
			Calendar anotherCalendar) {
		
		if ((oneCalendar != null) && (anotherCalendar != null)) {
			return ((oneCalendar.get(Calendar.ERA) == 
					anotherCalendar.get(Calendar.ERA)) &&
					(oneCalendar.get(Calendar.YEAR) == 
					anotherCalendar.get(Calendar.YEAR)) &&
					(oneCalendar.get(Calendar.DAY_OF_YEAR) == 
					anotherCalendar.get(Calendar.DAY_OF_YEAR)));
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Compare two Date objects to see if the fall on the same day.
	 * 
	 * @param oneDate A populated Date object
	 * @param anotherDate A populated Date object.
	 * @return True if the Date objects fall on the same day, false otherwise.
	 */
	private boolean isSameDay(Date oneDate, Date anotherDate) {
		
		if ((oneDate != null) && (anotherDate != null)) {
			Calendar oneCal = Calendar.getInstance();
			oneCal.setTime(oneDate);
			Calendar anotherCal = Calendar.getInstance();
			anotherCal.setTime(anotherDate);
			return isSameDay(oneCal, anotherCal);
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Return a singleton instance to the DateModelFactory object.
	 * 
	 * @return The DateModelFactory
	 */
	public static DateModelFactory getInstance() {
		return DateModelFactoryHolder.getFactorySingleton();
	}
	
	/**
	 * Compute a list of DayModel objects starting with a particular day and 
	 * going back in time the client specified number of days.
	 * 
	 * @param endDate End date for the model to compute (Note: end date is the 
	 * MOST recent date.)
	 * @param days The number of days to go back from the end date. 
	 * @return A list of DayModel objects.
	 */
	public List<DayModel> getModel(Date endDate, int days) {
		
		List<DayModel> dayModelList = new ArrayList<DayModel>();
		
		if (endDate != null) {
			if (days > 0) {
		
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				
				for (int day=0; day<days; day++) {
					
					DayModel dayModel = new DayModel(
							cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH));
					dayModelList.add(dayModel);
					cal.add(Calendar.DATE, -1);
					
				}
			}
		}
		return dayModelList;
	}
	
	/**
	 * Generate a list that contains a list of start/end times for days that 
	 * fall between the input start and end dates.
	 * 
	 * @param startDate Beginning date in the output list of DayModel objects. 
	 * @param endDate Last date in the output list of DayModel objects. 
	 * @return A list containing the start/end times for each day in the 
	 * input range.
	 */
	public List<DayModel> getModel(Date startDate, Date endDate) {
		
		List<DayModel> dayModelList = new ArrayList<DayModel>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		Date temp = cal.getTime();
		
		while (!isSameDay(temp, startDate)) {
			
			DayModel dayModel = new DayModel(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			dayModelList.add(dayModel);
			cal.add(Calendar.DATE, -1);
			temp = cal.getTime();
			
		}
		return dayModelList;
	}
	
	
	public static void main(String[] args) {
		DateModelFactory factory = new DateModelFactory();
		Date today = new Date();
		
		List<DayModel> model = factory.getModel(today, 30);
		System.out.println("Num elements created [ "
				+ model.size()
				+ " ].");
		for (DayModel day : model) {
			System.out.println(day.toString());
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, -30);
		model = factory.getModel(cal.getTime(), today);
		System.out.println("Num elements created [ "
				+ model.size()
				+ " ].");
		for (DayModel day : model) {
			System.out.println(day.toString());
		}
	}
	
	/** 
	 * Static inner class used to construct the factory singleton.  This
	 * class exploits that fact that inner classes are not loaded until they 
	 * referenced therefore enforcing thread safety without the performance 
	 * hit imposed by the use of the "synchronized" keyword.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class DateModelFactoryHolder {
		
		/**
		 * Reference to the Singleton instance of the factory
		 */
		private static DateModelFactory _factory = new DateModelFactory();
		
		/**
		 * Accessor method for the singleton instance of the factory object.
		 * 
		 * @return The singleton instance of the factory.
		 */
		public static DateModelFactory getFactorySingleton() {
			return _factory;
		}
	}
}
