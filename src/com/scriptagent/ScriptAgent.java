package com.scriptagent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

public class ScriptAgent extends Agent
{
	public final String agentName;
	public final boolean reportMetricsToServers;

	private Script[] scripts;


	public static final String GUID 	= "com.scriptagent";
	public static final String version 	= "0.0.1";
	
    private static final Logger logger = Logger.getLogger(ScriptAgent.class);
	
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
	@Override
	public void pollCycle()
	{
        long totalResponseTime = 0;
        long averageResponseTime = 0;        

        int successfulExecutions = 0;
        int unsuccessfulExecutions = 0;

		for(Script script : scripts)
		{   
            StringBuilder stdOut = new StringBuilder();

            long begin = System.currentTimeMillis();
            int exitValue = script.run(stdOut);
            totalResponseTime += (System.currentTimeMillis() - begin);
           
            if(exitValue != 0)
            {
                logger.error(stdOut.toString());
                unsuccessfulExecutions++;
            }
            else
            {
                try
                {
                    for(String line : stdOut.toString().split("\n"))
                    {
                        String [] metrics = parseMetrics(line);

                        String component = metrics[0];
                        String label = metrics[1];
                               
                        String value = metrics[2].substring(0, metrics[2].indexOf('['));
                        String units = metrics[2].substring(metrics[2].indexOf('[') + 1, metrics[2].length() - 1);
                
                        logger.info("Reporting: " + component + " " + label + " " + value + " " + units);
                        reportMetric(component + "/" + label, units,  Double.parseDouble(value));

                        successfulExecutions++;    
                    }
                }
                catch(ScriptAgentException scriptExcept)
                {
                    logger.error("ERROR: " + scriptExcept.getMessage());
                }
           }
       }
       averageResponseTime = (totalResponseTime / (successfulExecutions + unsuccessfulExecutions));
       reportMetric("ScriptAgentHealth" + "/SuccessfulExecutions", "Integer", successfulExecutions);
       reportMetric("ScriptAgentHealth" + "/UnsuccessfulExecutions", "Integer", unsuccessfulExecutions);
       reportMetric("ScriptAgentHealth" + "/TotalResponseTime", "milliseconds", totalResponseTime);
       reportMetric("ScriptAgentHealth" + "/AveragePerScriptResponseTime", "milliseconds", averageResponseTime);
	}

    public String [] parseMetrics(String s) throws ScriptAgentException
    {
        String [] metrics = new String[3];
    
        if(!s.isEmpty() && s.startsWith("Metrics:"))
        {
            s = s.substring(s.indexOf(":") + 1, s.length());
            if(s.split("/").length == 3)
            {
                metrics = s.split("/");
                for(String value : metrics)
                {
                   if(value == null || value.isEmpty())
                   {
                        throw new ScriptAgentException("One of the components returned null or empty string");
                   }    
                }    
            }
        }
       
        return metrics;
    }    
}
