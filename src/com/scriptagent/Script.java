package com.scriptagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.newrelic.metrics.publish.util.Logger;

public class Script
{
	private String path;
	private Logger logger = Logger.getLogger(Script.class);;

	public Script(String src)
	{
    	path = src;
	}
    
	public String[] run()
	{
        String command = path;
        
        Process proc; 
        BufferedReader stdInput, stdError;
        
        try
        {
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        }
		catch(IOException IOExcep)
        {
    		logger.error("ERROR: " + IOExcep.getMessage());
        }
        catch(InterruptedException IrqExecp)
        {
            logger.error("ERROR: " +IrqExecp.getMessage());
        }
        
        if(proc.exitValue() == 0)
        {
            parseMetric(stdInput);
        }
    }
   
    private String parseMetric(BufferedReader buffer)
    {
        List<String> metrics = new ArrayList<String>();
        
        String line;
        while((line = stdInput.readLine) != null)
        {
            if(!line.startsWith("#") && line.startsWith("Metric:"))
            {
                line = line.substring(line.indexOf(':') + 1, line.length()).trim();
                for(String value : line.split("/"))
                {
                
                }
            }
        }    
    }
         
    public String getPath()
	{
		return path;
	}
}
