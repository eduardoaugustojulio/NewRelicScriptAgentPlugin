package com.ricardosantos.scriptagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.newrelic.metrics.publish.util.Logger;

/**
 * 
 * @author Ricardo
 *
 */
public class Script
{
	private String scriptPath;
	private Logger logger = Logger.getLogger(Script.class);;

	public Script(String scriptPath)
	{
		this.scriptPath = scriptPath;
	}

	/**
	 * Executes the script, reads the output and returns a list of metrics.
	 * Script should output metrics to stdout with the following format:
	 * Component/myApp/myReportedMetric[units]
	 * Prefix/Category/Label[units]
	 * @return A list of SimpleAgentMetric instances with the output metrics.
	 */
	public List<SimpleAgentMetric> runScript()
	{
		List<SimpleAgentMetric> results = new ArrayList<SimpleAgentMetric>();
		
		String line;
		String l[] = new String[4];
		
		try
		{
			logger.debug("Going to run script: " + this.scriptPath);
			Process p = Runtime.getRuntime().exec(this.scriptPath);
			p.waitFor();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			
			logger.debug("Going to parse results.");
			while ((line = stdInput.readLine()) != null)
			{
				if(!line.startsWith("#") && line.startsWith("Metric:"))
				{
					try
					{
						// Remove the prefix "Metrics:"
						logger.debug("Going to parse line: " + line);
						line = line.substring(line.indexOf(':')+1, line.length()).trim();
						logger.debug("Going to parse line: " + line);
						l = metricStringIntoArray(line);
						results.add(new SimpleAgentMetric(l[0], l[1], l[2], l[3]));
					}
					catch(ScriptAgentException e)
					{
						logger.error(e.getMessage() + ". Could not parse metric line output from script execution. Line: " + line);
					}
				}
            }
			
			while ((line = stdError.readLine()) != null) {
				logger.error("Error reading text file: " + line);
			}
		}
		catch(IOException e)
		{
			logger.error("Failed to execute script, will keep tring: " + e.getMessage());
			results = null;
		}
		catch (InterruptedException e1)
		{
			logger.error("Failed to wait for script to finish: " + e1.getMessage());
			results = null;
		}

		
		return results;
	}

	/**
	 * Splits a metric string into its components.
	 * @param line Metric in the following format: Component/myApp/myReportedMetric[units]
	 * @return {Component, myApp, myReportedMetric, units}
	 * @throws ScriptAgentException 
	 */
	private String[] metricStringIntoArray(String line) throws ScriptAgentException
	{
		if(line==null)
		{
			throw new ScriptAgentException("The received metric line was null.");
		}
		
		String metricComponents[] = new String[4];
		
		// Split into { Component, myApp, myReportedMetric[units] }
		String[] regExResult = line.split("/");
		// Store { Component, myApp}
		metricComponents[0] = regExResult[0];
		metricComponents[1] = regExResult[1];
		// Store { myReportedMetric }
		metricComponents[2] = regExResult[2].substring(0, regExResult[2].indexOf('['));
		// Store { units }, removing the "]" in the end.
		metricComponents[3] = regExResult[2].substring(regExResult[2].indexOf('[')+1, regExResult[2].length()-1);
		
		// Check if we have any null or empty string values.
		if(metricComponents[0] == null || metricComponents[0].length()==0 ||
		   metricComponents[1] == null || metricComponents[1].length()==0 ||
		   metricComponents[2] == null || metricComponents[2].length()==0 ||
		   metricComponents[3] == null || metricComponents[3].length()==0 )
		{
			throw new ScriptAgentException("One of the components returned null or empty string. Original: \"" + line + "\", Parsed: {"
		                  + metricComponents[0]
                    + "," + metricComponents[1]
					+ "," + metricComponents[2]
					+ "," + metricComponents[3] + "}");
		}

		if(metricComponents[2].length()==0 || metricComponents[3].length()==0)
		{
			System.out.println("we have a 0...");
		}
			
		logger.debug("Split \"" + line + "\" into " + UtilityMethods.printArray(metricComponents));
		return metricComponents;
	}
	
	/**
	 * @return Returns the script path.
	 */
	public String toString()
	{
		return this.scriptPath;
	}

}
