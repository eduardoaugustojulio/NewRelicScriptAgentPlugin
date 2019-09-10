package com.scriptagent;

import java.util.List;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

public class ScriptAgent extends Agent
{
	public final String agentName;
	public final boolean reportMetricsToServers;
	public static final String GUID 	= "com.scriptagent";
	public static final String version 	= "0.0.1";
	
	private static final Logger logger = Logger.getLogger(ScriptAgent.class);
	
	private Script[] scripts;
	public ScriptAgent(String name, String[] paths, boolean reportMetricsToServers)
	{
		super(GUID, version);
		this.agentName = name;
		this.reportMetricsToServers = reportMetricsToServers;
        
        scripts = new Script[paths.length];
        for(int n = 0; n < paths.length; n++)
        {
            scripts[n] = new Script(paths[n]);
        }
	}

	@Override
	public void pollCycle()
	{
        int success, unsuccess;

		for(Script script : scripts)
		{
			script.run();
		}
	}

	@Override
	public String getAgentName()
	{
		return this.agentName;
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
}
