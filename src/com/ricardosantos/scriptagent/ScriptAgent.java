/**
 * 
 */
package com.ricardosantos.scriptagent;

import java.util.List;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

/**
 * @author Ricardo
 *
 */
public class ScriptAgent extends Agent
{
	public final String agentName;
	public final boolean reportMetricsToServers;
	public static final String GUID 	= "com.ricardosantos.scriptagent.ScriptAgent";
	public static final String version 	= "0.0.1";
	
	private static final Logger logger = Logger.getLogger(ScriptAgent.class);
	
	private Script[] scripts;
	private static final String HEALTH_METRIC_PREFIX = "ScriptAgentHealth";

	/**
	 * @param scriptPath List of the paths of the scripts to execute.
	 */
	public ScriptAgent(String agentName, String[] scriptPath, boolean reportMetricsToServers)
	{
		super(GUID, version);
		this.agentName = agentName;
		this.reportMetricsToServers = reportMetricsToServers;
		logger.debug("Instantiating agent: " + this.agentName + " v" + version);
		this.scripts = new Script[scriptPath.length];
		for (int i=0 ; i<scriptPath.length ; i++)
		{
			if (scriptPath[i] != null)
			{
				this.scripts[i] = new Script(scriptPath[i]);
				logger.debug("Instantiated script: " + scriptPath[i]);
			}
			else
			{
				logger.error("Script not valid, discarding it: " + scriptPath[i]);
			}
		}
	}

	/* This is called every minute.
	 * @see com.newrelic.metrics.publish.Agent#pollCycle()
	 */
	@Override
	public void pollCycle()
	{
		int successfulExecutions = 0;
		int unsuccessfulExecutions = 0;
		long totalResponseTime = 0, AVGResponseTime;
		long respTime;
		for (Script s : scripts)
		{
			respTime = System.currentTimeMillis();
			List<SimpleAgentMetric> metrics = s.runScript();
			respTime = System.currentTimeMillis() - respTime;
			totalResponseTime += respTime;
			if(metrics == null || metrics.size()==0)
			{
				logger.debug("Script failed: " + s);
				unsuccessfulExecutions++;
			}
			else
			{
				logger.debug("Ran script: " + s + ", Resp time: " + (respTime) + "ms");
				successfulExecutions++;
				for (SimpleAgentMetric m : metrics)
				{
					reportMetric(m);
					logger.debug("Reported metric: " + m);
				}
			}
		}
		AVGResponseTime = totalResponseTime/(successfulExecutions+unsuccessfulExecutions);
		logger.debug("Successful executions: " + successfulExecutions + ", Unsuccessful executions: " + unsuccessfulExecutions + ", AVG resp time per script execution: " + AVGResponseTime + "ms");
		reportMetric(HEALTH_METRIC_PREFIX + "/SuccessfulExecutions", "Integer", successfulExecutions);
		reportMetric(HEALTH_METRIC_PREFIX + "/UnsuccessfulExecutions", "Integer", unsuccessfulExecutions);
		reportMetric(HEALTH_METRIC_PREFIX + "/TotalResponseTime", "milliseconds", totalResponseTime);
		reportMetric(HEALTH_METRIC_PREFIX + "/AveragePerScriptResponseTime", "milliseconds", AVGResponseTime);
	}

	/* (non-Javadoc)
	 * @see com.newrelic.metrics.publish.Agent#getAgentName()
	 */
	@Override
	public String getAgentName()
	{
		return this.agentName;
	}
	
	/**
	 * ToString method for ScriptAgent.
	 * @return The string representation of a ScriptAgent.
	 */
	public String toString()
	{
		String scriptsOutput = "{";
		for (int i=0 ; i<scripts.length ; i++)
		{
			scriptsOutput += scripts[i];
			if (i<scripts.length-1)
				scriptsOutput += ",";
		}
		scriptsOutput += "}";
		return this.agentName + " v" + version + ", Reporting metrics to Servers: " + reportMetricsToServers + ", Scripts: " + scriptsOutput;
	}
	
	@Override
	public void reportMetric(String metricName, String units, Number value)
	{
		if (reportMetricsToServers)
			super.reportMetric(metricName, units, value);
	}

	@Override
	public void reportMetric(String metricName, String units, int count, Number value, Number minValue, Number maxValue,
			Number sumOfSquares)
	{
		if (reportMetricsToServers)
			super.reportMetric(metricName, units, count, value, minValue, maxValue, sumOfSquares);
	}
	
	/**
	 * Reports the passed metric.
	 * @param m The SimpleAgentMetric to report.
	 */
	public void reportMetric(SimpleAgentMetric m)
	{
		if (reportMetricsToServers)
			reportMetric(m.getMetricCategory() + "/" + m.getMetricLabel(), m.getMetricUnits(), m.getMetricValue());
	}
}
