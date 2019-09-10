package com.scriptagent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

public class ScriptAgentFactory extends AgentFactory
{
	private static final Logger logger = Logger.getLogger(ScriptAgentFactory.class);

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
		
        JSONArray scripts = (JSONArray)properties.get("scripts");

        return new ScriptAgent(agentName, scriptsToExecute(scripts), Boolean.valueOf(reportMetricsToServers));
    }
	
	private String[] scriptsToExecute(JSONArray scripts) throws ConfigurationException
	{
        if (scripts == null || scripts.size() == 0) 
        {
            throw new ConfigurationException("The list of 'scripts' in plugin.json it's empty!");
        }
   
        String[] pathArray = new String[scripts.size()];
        for (int n = 0 ; n < scripts.size() ; n++)
        {
            String path = (String) scripts.get(n);    
            File f = new File(path);
        	
            if (!f.exists() || f.isDirectory())
        	{
        		logger.error("Script: '" + path + "' can not be executed!");
        	}
            pathArray[n] = path;
        }
        
        if(pathArray.length == 0)
        {
            throw new ConfigurationException("There is no executable 'scripts' in plugin.json!");
        }

        return pathArray;
	}
}
