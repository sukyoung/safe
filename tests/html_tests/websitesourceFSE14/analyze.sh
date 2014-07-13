#!/bin/sh

#######################################
# Settings
#######################################
command="webapp-bug-detector"
options="-disableEvent -timeout 600"
target="main.htm"
out="out.txt"
threads=5
#######################################



#######################################
# Script Code
#######################################

# Get this script directory
scriptDir=$PWD/$0
scriptDir="${scriptDir%/*}"/

# Collect website directories
websiteCount=0
websiteNames=
websiteDirs=
for directory in `ls -d $scriptDir*/`
do
	directory="${directory%/*}"
	directory="${directory##*/}"
	websiteNames[$WebsiteCount]=$directory
	websiteDirs[$WebsiteCount]=$scriptDir$directory
	((WebsiteCount++))
done
echo "* Website count: "$WebsiteCount

# Create tokens
for (( i = 1 ; i <= threads ; i++ ))
do
	touch $scriptDir"token"$i".temp"
done

# Run function
function runAnalysis {
	i=$1
	tokenname=$2
	websiteName=${websiteNames[$i]}
	websiteDir=${websiteDirs[$i]}
	websiteHTML=$websiteDir/$target

	echo "*" Analyzing"("$(($i + 1))/$WebsiteCount")"... $websiteName \"jsaf $command $options $websiteHTML\"
	`jsaf $command $options $websiteHTML > $websiteDir/$out`
	echo "*" Finished! $websiteName

	touch $tokenname

	exit
}

# For each website
for i in ${!websiteNames[*]}
do
	run=1
	while (( run == 1 ))
	do
		for (( j = 1 ; j <= $threads ; j++ ))
		do
			tokenname=$scriptDir"token"$j".temp"
			if [ -f $tokenname ]
			then
				rm $tokenname
				runAnalysis $i $tokenname &
				run=0
				break
			fi
		done
		# sleep 1 second if there is no available token
		if (( run == 1 )); then sleep 1; fi
	done
done

# Wait
run=1
while (( run == 1 ))
do
	run=0
	for (( i = 1 ; i <= threads ; i++ ))
	do
		tokenname=$scriptDir"token"$i".temp"
		if [ ! -f $tokenname ]
		then
			run=1
		fi
	done
	if (( run == 1 )); then sleep 1; fi
done

# Delete tokens
rm -f "$scriptDir"token*.temp

# End
echo "* All finished!"
