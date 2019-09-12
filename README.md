# New Relic Script Agent Plugin v0.0.2 #

## Description ##
This plugin is designed to execute custom scripts and read their output from stdout, parse the output into metrics
and upload these metrics into your New Relic cloud account.

## Plugin requirements ##
- Must have a valid New Relic account.
- Must have permission to execute the custom scripts, e.g. sudo scripts must run under a sudoer account.
- JRE installed, preferably the latest version.

## How scripts are executed ##
The Script Plugin polling thread will execute once every 60 seconds, and within these 60 seconds it will execute
sequentially all the scripts it finds in plugin.json.

**NOTE** This plugin has not been tested with scripts taking a total of over 60 seconds to execute.

Each time a script is executed, the plugin executing thread will wait until that script completes. After a script
completes the plugin poling thread will proceed to execute the next script or it will terminate if there are no more
scripts to execute.

**NOTE** You need to ensure that your scripts have the appropriate permissions to run, e.g. scripts that make use of
sudo must be able to run without requiring a password input as the poling thread will not pass any input to the sudo
command line when the script hangs.

The poling thread will read stdout for metrics, and stderr for errors. Errors from stderr will be reported to
logger.error, which will come out as ERROR messages in the plugin's stdout and in plugin/logs/newrelic_plugin.log.

## Writing your custom scripts ##
As mentioned above, the poling thread which will execute your scripts will listen to stdout for metrics and stderr 
and errors. Your scripts should be written to execute and terminate in that one execution.

No specific format is required for stderr, all the text you report to stderr will be passed as is to the plugin's stdout 
and plugin/logs/newrelic_plugin.log as a ERROR message. For stdout, the plugin is expecting metrics to have the following prefix:

```
Metric:
```

All stdout output that does not start with the above mentioned prefix will be discarded.

Metrics must have the following format:

![alt text](https://docs.newrelic.com/sites/default/files/thumbnails/image/support%20metric%20name%20syntax.png "Metric syntax")

[More info here.](https://docs.newrelic.com/docs/plugins/plugin-developer-resources/developer-reference/metric-naming-reference "Metric naming reference")

Here are some examples of correct metrics:
- `Metric:OSbasicMetrics/CPUSystem/77[percentage]`
- `Metric:MyCoffeeMachineScript/CurrentCoffeeAmount/120[ml]`
- `Metric:MyCoffeeMachineScript/CurrentJugLevel/80[percentage]`

Some examples of incorrect metrics which will be discarded as they do not have the `Metric:` prefix:
- `OSbasicMetrics/CPUSystem/77[percentage]`
- `MyCoffeeMachineScript/CurrentCoffeeAmount/120[ml]`

Some examples of incorrect metrics which will result in an error:
- `Metric:OSbasicMetrics/CPUSystem/abc[percentage]` Incorrect because 'abc' is not a numeric value.
- `Metric:MyCoffeeMachineScript/CoffeMetrics/CurrentCoffeeAmount/120[ml]` Incorrect because it has one too many fields.

Naming your metrics is vital to be able to correctly fecth them in New Relic's UI. Grouping a set of metrics into the
same chart is only possible via the use of wildcards. More on this in the following section.

## Fetching your metrics in New Relic's Plugins UI ##
All of the metric sections can be used to fetch metrics in the New Relic's UI, to filter and group, therefore it is
vital that you use a suitable convention when naming your metrics.

You will be able to display metrics in the Plugins' UI by adding charts and tables to your dashboards. Once you click
on "Add chart or table", you will be presented with an Edit chart form. In this form, the section "Metric(s)" will let
you chose which metrics to display in your chart/table.

A metric reported with the following format:
```
Metric:ScriptAgentHealth/TotalResponseTime/32[ms]
```
Will be accessible in the "Metric(s)" field by:
```
Component/ScriptAgentHealth/TotalResponseTime[ms]
```

If the goal is to group several metrics into the same chart/table, then here's an example:
The metrics reported as follows:

```
Metric:OSbasicMetrics/CPUuser/35[ms]
Metric:OSbasicMetrics/CPUsystem/15[ms]
Metric:OSbasicMetrics/CPUidle/50[ms]
```

Can all be displayed in the same chart/table by using the following query in the "Metric(s)" field:

```
Component/OSbasicMetrics/CPU*
```

## Metrics source documentation ##
There are two types of metrics that this plugin will report to your New Relic account:
- The custom metrics your script(s) will output.
- Health metrics that report on the plugin's script health and response times.

### Script Agent Health Metrics ###

Below is a summary of the health metrics currently being reported to the New Relic servers:

| Metric name                                      | Type        | Description                                                                                                                                                         |
| ------------------------------------------------ | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `ScriptAgentHealth/SuccessfulExecutions`         | `[Integer]` | Number of successfully executed scripts for each polling thread execution.                                                                                          |
| `ScriptAgentHealth/UnsuccessfulExecutions`       | `[Integer]` | Number of unsuccessfully executed scripts for each polling thead execution.                                                                                         |
| `ScriptAgentHealth/TotalResponseTime`            | `[milliseconds]` | The sum of all response time executions of each individual script per polling thread execution. This will count both successful and unsuccessful script executions. |
| `ScriptAgentHealth/AveragePerScriptResponseTime` | `[milliseconds]` | The average response time of all script executions per polling thread execution. This will count both successful and unsuccessful script executions.                |

## Installation ##
### To simply run ###
Use New Relic's Platform Installer (NPI).

### For development, to extend the plugin ###
Download the Java project from GitHub, add it to Eclipse (New Project -> Java Project -> select the root directory of
the downloaded GitHub project), create a Run Configuration where you set `com.scriptagent.Main` as the main class.

## Configuration ##
Configuration is done via two files: newrelic.json & plugin.json.

### newrelic.json ###
This json file contains the following fields:

| Field                 | Description                                                                                                | Possible value(s)            |
| --------------------- | ---------------------------------------------------------------------------------------------------------- | ---------------------------- |
| "license_key"         | Your license key. This license key is what will link the plugin execution to a specific New Relic account. | "your_licence_key"           |
| "log_level"           | The level of lot output to come out in the Plugins' stdout and log file.                                   | "debug"/"info"               |
| "log_file_name"       | The plugin's log file name.                                                                                | "your_log_file_name.log"     |
| "log_file_path"       | The plugin's log file path.                                                                                | "your_log_file_directory"    |
| "log_limit_in_kbytes" | Limit the size of your log file, 0 for no limit.                                                           | "0"/"your_max_log_file_size" |

### plugin.json ###
This json file contains the following fields:

| Field                    | Description                                                                                     | Possible value(s)              |
| ------------------------ | ----------------------------------------------------------------------------------------------- | ------------------------------ |
| "agentName"              | The name of your agent, which will be used in New Relic's UI to identify your agent.            | Just a string with a name.     |
| "reportMetricsToServers" | Decides if the metrics captured from the scripts will be sent to your New Relic account or not. | "true"/"false"                 |
| "scriptsToExecute"       | This field is an array with ALL the scripts that you want to execute.                           | "the_full_path_of_your_script" |

## Troubleshooting ##
The first step to troubleshooting is to change the field "log_level" in newrelic.json to "debug". The output of the
script processing code is quite comprehensive so if there are any issue with executing a script, the output should be
clear.

While writing the scripts and testing them, you will most likely want to clear the captured metrics and start over
(Account Settings -> Connected Agents -> Clear Metric Data). I found that this sometimes breaks the next execution of
the Plugin, where even though the plugin is reporting metrics to New Relic and is getting a 200 response, no metrics
are displayed in the UI. This seems to be due to the way Plugin metrics are cached in New Relic's servers, and how/when
this cache is cleared. The best solution I came across was to switch off the plugin, clear the metrics again and wait
for a while, up to an hour, and then start the plugin again.

## Sample scripts ##
### OSbasicMetrics.sh ##
Reports a few basic OS metrics.

| Metric name                 | Type           | Description  |
| --------------------------- | -------------- | ------------ |
| `OSbasicMetrics/CPUUser`    | `[percentage]` | User CPU.    |
| `OSbasicMetrics/CPUSystem`  | `[percentage]` | System CPU.  |
| `OSbasicMetrics/CPUIdle`    | `[percentage]` | Idle CPU.    |
| `OSbasicMetrics/MemoryUsed` | `[Megabytes]`  | Used memory. |
| `OSbasicMetrics/MemoryFree` | `[Megabytes]`  | Free memory. |

*** NOTE 1 *** Both `MemoryUsed` and `MemoryFree` run using sudo so if you're not seeing these metrics in your
dashboards, it's likely that your script is failing to execute as sudo. If sudo fails, this will NOT show up as an error
in the plugin's log output.

*** NOTE 2 *** Both `MemoryUsed` and `MemoryFree` are being obtained from the `systemstats` command, which I am not sure
give an accurate representation of these metrics. Comments/suggestions are welcome :-)

### CloudStorageLocalUsage.sh ##
Simply checks the disk usage for your DropBox and Google Drive folders. Make sure to edit this script and update the
DropBox and Google Drive paths.

| Metric name                                    | Type          | Description                                                   |
| ---------------------------------------------- | ------------- | ------------------------------------------------------------- |
| `CloudStorageLocalUsage/DropBoxLocalUsage`     | `[Megabytes]` | The total disk space your local DropBox files are using.      |
| `CloudStorageLocalUsage/GoogleDriveLocalUsage` | `[Megabytes]` | The total disk space your local Google Drive files are using. |

## Disabling and uninstalling ##
Switch off / kill the java plugin process. To clear all gathered metrics, follow these steps:
Account Settings -> Connected Agents -> Clear Metric Data

## Support resources ##
[New Relic's Plugins](http://www.newrelic.com/plugins)

[New Relic's Plugins documentation](https://docs.newrelic.com/docs/plugins/plugins-new-relic)
