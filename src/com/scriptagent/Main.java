package com.scriptagent;

import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class Main
{
    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            runner.add(new ScriptAgentFactory());
            runner.setupAndRun(); 
        } catch (ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
