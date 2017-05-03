package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import mil.nga.bundler.model.Job;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Class used to generate the data model for display in a prime faces Chart UI
 * component.  This particular class generates a chart showing how much data
 * was transferred each day for the past 30 days.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@RequestScoped
public class ViewDataTransferredChartModel 
        extends ChartModel 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 6063617665328494098L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(ViewDataTransferredChartModel.class);
    
    /**
     * The bar chart model that will be displayed.
     */
    private BarChartModel barModel;
    
    /**
     * This method serves as the constructor which will create and populate 
     * the internal BarChartModel object. 
     */
    @PostConstruct
    public void initialize() {
        List<Job> jobs = getJobList();
        if ((jobs != null) && (jobs.size() > 0)) {
            buildBarModel(jobs);
        }
        else {
            LOGGER.error("Unable to find any jobs submitted in the "
                    + "last 30 days.");
        }
    }
    
    /** 
     * Getter method for the BarChartModel that was created on construction.
     * @return The BarChartModel
     */
    public BarChartModel getBarModel() {
        return barModel;
    }
    
    /**
     * Determines the maximum amount of data transferred on a single
     * day during the past 30 days.
     * 
     * @param map Map containing the calculated number of jobs submitted per
     * day.
     * @return The maximum amount of data transferred on a single day.
     */
    private long getMaxSizeJobs(Map<String, Long> map) {
        long max = 0;
        for (String key : map.keySet()) {
            Long val = map.get(key);
            if (val.longValue() > max) {
                max = val.longValue();
            }
        }
        return max;
    }
    
    private void buildBarModel(List<Job> jobs) {
        
        DateModelFactory factory = DateModelFactory.getInstance();
        List<DayModel>   dayModel = factory.getModel(new Date(), 30);
        Map<String, Long> map = new LinkedHashMap<String, Long>();
        
        for(DayModel day : dayModel) {
            
            String key   = day.getDayString();
            long   value = 0;
            for (Job job : jobs) {
                if ((job.getStartTime() > day.getStartTime()) &&
                        (job.getStartTime() < day.getEndTime())) {
                    value += job.getTotalSize();
                }
            }
            map.put(key, new Long(value / (1024 * 1024) ));
        }
        
        barModel = new BarChartModel();
        ChartSeries jobsSubmitted = new ChartSeries();
        jobsSubmitted.setLabel("Data Transferred (Last 30 Days)");
        
        for (String key : map.keySet()) {
            jobsSubmitted.set(key, map.get(key).longValue());
            System.out.println("Key => [ "
                    + key
                    + " ], value => [ "
                    + map.get(key).longValue()
                    + " ].");
        }
        barModel.addSeries(jobsSubmitted);
        
        barModel.setTitle("Data Transferred (Last 30 Days)");
        barModel.setLegendPosition("ne");
        
        // Set up the X Axis
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Date");
        
        // Set up the Y axis
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Data Transferred (MB)");
        yAxis.setMin(0);
        yAxis.setMax(getMaxSizeJobs(map));
        
    }
}
