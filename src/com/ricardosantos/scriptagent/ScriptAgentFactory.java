/**
 * 
 */
package com.ricardosantos.scriptagent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

/**
 * Script agent factory.
 * @author Ricardo
 */
public class ScriptAgentFactory extends AgentFactory
{
	private static final Logger logger = Logger.getLogger(ScriptAgentFactory.class);

	/* (non-Javadoc)
	 * @see com.newrelic.metrics.publish.AgentFactory#createConfiguredAgent(java.util.Map)
	 */
	@Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
		
		String agentName = (String) properties.get("agentName");
		String reportMetricsToServers = (String) properties.get("reportMetricsToServers");
		if (agentName == null)
		{
            throw new ConfigurationException("'agentName' in plugin.json file cannot be null.");
		}
		if (reportMetricsToServers == null)
		{
			throw new ConfigurationException("'reportMetricsToServers' in plugin.json file cannot be null");
		}
		if (Boolean.valueOf(reportMetricsToServers) == null)
		{
			throw new ConfigurationException("'reportMetricsToServers' must have the value 'true' or 'false', not " + reportMetricsToServers);
		}
		
        JSONArray scripts = (JSONArray)properties.get("scriptsToExecute");

        return new ScriptAgent(agentName, scriptsToExecute(scripts), Boolean.valueOf(reportMetricsToServers));
    }
	
	/**
	 * Extract and validate the script paths from the JSONArray.
	 * @param scripts The array with the script paths.
	 * @return An array with the validated path strings.
	 * @throws ConfigurationException If the passed list is null or has size 0, or if any of the scripts is not a valid file.
	 */
	private String[] scriptsToExecute(JSONArray scripts) throws ConfigurationException
	{
        if (scripts == null)
        {
            throw new ConfigurationException("'scriptsToExecute' in plugin.json file cannot be null.");
        }
        if (scripts.size() == 0)
        {
            throw new ConfigurationException("'scriptsToExecute' in plugin.json file cannot have size 0.");
        }
        List<String> scriptsToExecute = new ArrayList<String>();
        for (int i=0 ; i<scripts.size() ; i++)
        {
        	File f = new File((String)scripts.get(i));
        	if (!f.exists() || f.isDirectory())
        	{
        		logger.error("Script '" + (String)scripts.get(i) + "' does not exist or it is not a valid script. I will keep trying to execute it in every turn so you can fix it without restarting the agent.");
        	}
           	scriptsToExecute.add((String)scripts.get(i));
        }
        
        if (scriptsToExecute.size() == 0 ) {
            throw new ConfigurationException("'scriptPath' cannot be null, you must have at least one script for the ScriptAgent to run.");
        }

        String scriptsStr = "{";
        for (int i=0 ; i<scriptsToExecute.size() ; i++)
		{
        	scriptsStr += scriptsToExecute.get(i);
        	if(i<scriptsToExecute.size()-1)
        		scriptsStr += ",";
		}
        scriptsStr += "}";
        logger.debug("Obtained scripts: " + scriptsStr);

        
		// Moving into an Array for added performance.
		String[] scriptsArr = new String[scriptsToExecute.size()];
		for (int i=0 ; i<scriptsToExecute.size() ; i++)
		{
			scriptsArr[i] = scriptsToExecute.get(i);
		}

        return scriptsArr;
	}

}
