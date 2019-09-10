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

	public void run()
	{
        BufferedReader stdInput, stdError;
            
        try
        {
            Process proc = Runtime.getRuntime().exec(path);
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
    }

    public String getPath()
	{
		return this.path;
	}

}
