package com.ricardosantos.scriptagent;

/**
 * Representation of a simple metric.
 * @author Ricardo
 */
public class SimpleAgentMetric
{
	private String 	metricCategory;
	private String 	metricLabel;
	private double 	metricValue;
	private String	metricUnits;

	/**
	 * Creates a Simple Metric
	 * @param metricCategory The category of the metric.
	 * @param metricLabel The label of the metric.
	 * @param metricValue The value of the metric, a double.
	 * @param metricUnits The units of the metric.
	 */
	public SimpleAgentMetric(String metricCategory, String metricLabel, String metricValue, String metricUnits)
	{
		this.metricCategory = metricCategory;
		this.metricLabel = metricLabel;
		this.metricValue = Double.parseDouble(metricValue);
		this.metricUnits = metricUnits;
	}
	
	/**
	 * The metric category.
	 * @return The metric category.
	 */
	public String getMetricCategory()	{	return metricCategory;	}

	/**
	 * The metric label.
	 * @return The metric label.
	 */
	public String getMetricLabel()		{	return metricLabel;		}

	/**
	 * The metric value.
	 * @return The metric value.
	 */
	public double getMetricValue() 		{	return metricValue;		}
	
	/**
	 * The metric unit.
	 * @return The metric unit.
	 */
	public String getMetricUnits()		{	return metricUnits;		}

	/**
	 * @return The string representation of the instance.
	 */
	public String toString()
	{
		return metricCategory + "/" + metricLabel + "/" + metricValue + "[" + metricUnits + "]";
	}

}
