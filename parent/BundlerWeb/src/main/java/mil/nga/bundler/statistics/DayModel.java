package mil.nga.bundler.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Simple data structure used to abstract the start/end times of a given day
 * in milliseconds from epoch.  This was created for use in conjunction with 
 * the creation of bar charts for displaying the bundler statistics 
 * information.  
 * 
 * @author L. Craig Carpenter
 */
public class DayModel {

    private SimpleDateFormat sdf          = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
    private SimpleDateFormat dayFormatter = new SimpleDateFormat("MM/dd");
    private long             startTime    = 0L;
    private long             endTime      = 0L;
    
    /**
     * The default constructor constructs a DayModel for the current day.
     */
    public DayModel() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        setStartTime(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        setEndTime(cal.getTime());
    }
    
    /**
     * Construct a DayModel object for the input date.
     * 
     * @param year The year in int format.
     * @param month The month in int format.
     * @param day The day in int format.
     */
    public DayModel(int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        setStartTime(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        setEndTime(cal.getTime());
    }
    
    /**
     * Construct a String containing only the day part of the day this 
     * object models.
     * @return The day part (i.e. MM/DD/YYYY)
     */
    public String getDayString() {
        return dayFormatter.format(getStartDate());
    }
    
    /**
     * Getter method for the time at the end of a given day.
     * @return The time at the end of a given day.
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Getter method for the time at the end of a given day.  This method
     * returns a Date object.
     * @return The time at the end of a given day.
     */
    public Date getEndDate() {
        return new Date(endTime);
    }
    
    /**
     * Getter method for the time at the start of a given day.
     * @return The time at the start of a given day.
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Getter method for the time at the start of a given day.  This method
     * returns a Date object.
     * @return The time at the start of a given day.
     */
    public Date getStartDate() {
        return new Date(startTime);
    }
    
    /**
     * Setter method for the time at the end of a given day.
     * @param time The time at the end of a given day.
     */
    public void setEndTime(Date time) {
        endTime = time.getTime();
    }
    
    /**
     * Setter method for the time at the start of a given day.
     * @param time The time at the start of a given day.
     */
    public void setStartTime(Date time) {
        startTime = time.getTime();
    }
    
    /**
     * Overridden method to convert the internal members to a String
     * representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start Time => [ ");
        sb.append(sdf.format(getStartDate()));
        sb.append(" ], End Time => [ ");
        sb.append(sdf.format(getEndDate()));
        sb.append(" ].");
        return sb.toString();
    }
    
}
