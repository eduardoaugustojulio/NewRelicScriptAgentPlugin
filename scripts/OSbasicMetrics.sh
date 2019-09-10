
############  XXX  XXXX    XX  XX
############ XX    XX XX   XX  XX
############ XX    XX  XX  XX  XX
############ XX    XX XX   XX  XX
############ XX    XXXX    XX  XX
############ XX    XX      XX  XX
############  XXX  XX       XXXX

# Capture the current CPU metrics 1 time with 1 second interval
cpucmd=`sar -o ~/output.sar 1 1 | grep -v -e '^$' | grep -vwE "usr|Average"`
# Delete the temporary file created by the sar command
`rm ~/output.sar`

# Split the output string into its several components
time=`echo "${cpucmd}" | awk '{ print $1 }'`
usrcpu=`echo "${cpucmd}" | awk '{ print $2 }'`
nicecpu=`echo "${cpucmd}" | awk '{ print $3 }'`
syscpu=`echo "${cpucmd}" | awk '{ print $4 }'`
idlecpu=`echo "${cpucmd}" | awk '{ print $5 }'`

# Test output, commented by default
#echo Time    : ${time}
#echo Usr CPU : ${usrcpu}
#echo Nice CPU: ${nicecpu}
#echo Sys CPU : ${syscpu}
#echo Idle CPU: ${idlescpu}

# Report the metrics
echo Metric:OSbasicMetrics/CPUUser/${usrcpu}[percentage]
echo Metric:OSbasicMetrics/CPUSystem/${syscpu}[percentage]
echo Metric:OSbasicMetrics/CPUIdle/${idlecpu}[percentage]

############ XXX   XXX  XXXXX  XXX   XXX   XXXX   XXXX    XX  XX
############ XXXX XXXX  XX     XXXX XXXX  XX  XX  XX XX    XXXX
############ XX XXX XX  XX     XX XXX XX  XX  XX  XX  XX    XX
############ XX     XX  XXXX   XX     XX  XX  XX  XX XX     XX
############ XX     XX  XX     XX     XX  XX  XX  XXXX      XX
############ XX     XX  XX     XX     XX  XX  XX  XX XX     XX
############ XX     XX  XXXXX  XX     XX   XXXX   XX  XX    XX

## NOTE!!! systemstats requires sudo access, so you need to make sure this script is executed by a user that is part
##         of the sudoers list. If prompted for password, the script will report empty memory metrics.

memorystats=`sudo systemstats | grep -A 5 "Memory Summary" | grep 'Total\|Free'`
totalmemory=`echo ${memorystats} | awk '{ print $2 }'`
freememory=`echo ${memorystats} | awk '{ print $5 }'`
usedmemory=`echo "${totalmemory}-${freememory}" | bc`
echo Metric:OSbasicMetrics/MemoryUsed/${usedmemory}[Megabytes]
echo Metric:OSbasicMetrics/MemoryFree/${freememory}[Megabytes]
