package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
 * component.  This particular class generates a chart showing how many bundler 
 * jobs were submitted each day for the past 30 days.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@RequestScoped
public class ViewJobsSubmittedChartModel 
		extends ChartModel implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 6063617665328494098L;

	/**
	 * Static logger for use throughout the class.
	 */
	static final Logger LOGGER = 
			LoggerFactory.getLogger(ViewJobsSubmittedChartModel.class);
	
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
	 * Custom data tip.
	 * @return HTML string template for displaying data tips.
	 */
	public String getDataTipFormat() {
		return "<span>No. of Jobs %s</span>";
	}
	
	/**
	 * Determines the largest number of jobs that were submitted on a single
	 * day during the past 30 days.
	 * @param map Map containing the calculated number of jobs submitted per
	 * day.
	 * @return The maximum number of jobs submitted on a single day.
	 */
	private int getMaxNumJobs(Map<String, Integer> map) {
		int max = 0;
		for (String key : map.keySet()) {
			Integer val = map.get(key);
			if (val.intValue() > max) {
				max = val.intValue();
			}
		}
		return max;
	}
	
	/**
	 * This method does the heavy lifting required to construct the bar chart.
	 * It creates the Chart, labels, axis, etc. then populates the chart. 
	 * 
	 * This method will create a model documenting the start and end times 
	 * for each day for the past 30 days.  We then utilize the list of input 
	 * jobs to determine how many jobs were submitted on each day.
	 *  
	 * @param jobs List of jobs that were submitted during the last 30 days.
	 */
	private void buildBarModel(List<Job> jobs) {
		
		DateModelFactory     factory  = DateModelFactory.getInstance();
		List<DayModel>       dayModel = factory.getModel(new Date(), 30);
		Map<String, Integer> map  = new LinkedHashMap<String, Integer>();
		
		if ((jobs != null) && (jobs.size() > 0)) {
			for(DayModel day : dayModel) {
				
				String key   = day.getDayString();
				int    value = 0;
				for (Job job : jobs) {
					if ((job.getStartTime() > day.getStartTime()) &&
							(job.getStartTime() < day.getEndTime())) {
						value++;
					}
				}
				map.put(key, new Integer(value));
			}
			
			barModel = new BarChartModel();
			ChartSeries jobsSubmitted = new ChartSeries();
			jobsSubmitted.setLabel("Jobs Submitted (Last 30 Days)");
			
			// Add the data to the chart model
			for (String key : map.keySet()) {
				jobsSubmitted.set(key, map.get(key).intValue());
			}
			
			barModel.addSeries(jobsSubmitted);
			barModel.setTitle("Bundler Jobs Submitted (Last 30 Days)");
			barModel.setLegendPosition("ne");
			
			// Set up the X Axis
			Axis xAxis = barModel.getAxis(AxisType.X);
			xAxis.setLabel("Date");
			
			// Set up the Y Axis
			Axis yAxis = barModel.getAxis(AxisType.Y);
			yAxis.setLabel("Number of Jobs Submitted");
			yAxis.setMin(0);
			yAxis.setMax(getMaxNumJobs(map));
			
		}
		else {
			LOGGER.error("Unable to find any jobs submitted in the "
					+ "last 30 days.");
		}
	}
}
