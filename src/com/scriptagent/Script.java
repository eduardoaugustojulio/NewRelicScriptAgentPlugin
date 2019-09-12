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
    
	public int run(StringBuilder stdOut)
	{
        int exitValue = -1;
        
        Process proc; 
        BufferedReader stdInput, stdError;
        String cmd = path;
           
        try
        {
            proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();

            exitValue = proc.exitValue();

            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

               
            String line = null;
            if(exitValue == 0)
            {
                while((line = stdInput.readLine()) != null) 
                {
                    stdOut.append(line + "\n");
                }
            }
            else
            {
                while((line = stdError.readLine()) != null)
                {
                    stdOut.append(line + "\n");
                }
            }
        }
		catch(IOException IOExcept)
        {
    		logger.error("ERROR: " + IOExcept.getMessage());
        }
        catch(InterruptedException IrqExcept)
        {
            logger.error("ERROR: " +IrqExcept.getMessage());
        }
        return exitValue;
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
