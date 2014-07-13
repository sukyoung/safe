#!/bin/bash

################################################################################
#    Copyright (c) 2012-2014, S-Core, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

confirm() {
	read -n1 -p "$1([y]/n)?" key
	echo
	key=$(tr a-z A-Z <<<"$key")
	case $key in
		N)return 0;;
		*)return 1;;
	esac
}

colorize() {
	read -r headers
	echo -e "$headers"

	printf '%*s\n' "${#headers}" ' ' | tr ' ' "="
	declare -i i
	while read line;do
		i=$i+1
		if (( $i%2 == 1 )); then
			msg text_s "$line"
		else
			msg text "$line"
		fi
	done
}

timer() {
	declare -i c
	c=0
	while [ "$c" -lt "$1" ];do
		kill -0 $2 2> /dev/null
		if [ $? -eq 0 ];then
			c=$c+1
			sleep 1
		else
			echo "[ Success ]"
			exit
		fi
	done
	echo "[ Time out: over $1 sec ]"
	
	killtree $2 9
	exit
}

ctrl_c() {
	echo "** CTRL-C **"
}

killtree() {
	local _pid=$1
	local _sig=${2-TERM}
	kill -stop ${_pid} 2> /dev/null
	for _child in $(ps ax -o "pid= ppid=" | awk "{ if ( \$2 == ${_pid} ) { print \$1 }}");do 
		killtree ${_child} ${_sig}
	done
	kill -${_sig} ${_pid} 2> /dev/null
}

color_code=(
	"\033[1;35m" # text_s
       	"\033[37m"   # text
	"\033[1;32m" # info_s
	"\033[0;32m" # info
	"\033[1;31m" # warn_s
	"\033[0;31m" # warn
	)

msg() {
	if [ -n "$NO_ANSI" ];then
		shift
		echo -e "$@"
	else
		c=$1
		shift
		case $c in
			text_s) echo -e "${color_code[0]}$@\033[m";;
			text) echo -e "${color_code[1]}$@\033[m";;
			info_s) echo -e "${color_code[2]}$@\033[m";;
			info) echo -e "${color_code[3]}$@\033[m";;
			warn_s) echo -e "${color_code[4]}$@\033[m";;
			warn) echo -e "${color_code[5]}$@\033[m";;
			*) echo -e "$@";;
		esac
	fi	
}

pp_number() {
	if [[ -z "$1" ]];then
		echo -n "_"
	else
		[[ -n "$2" ]] || echo -n "$1"
		[[ -z "$2" ]] || echo -n "$(printf "%.$2f" $1)"
	fi
}

pp_diffnumber() {
	if [[ -z "$1" ]];then
		echo -n "_"
	else
		[[ -n "$2" ]] || echo -n "$1"
		[[ -z "$2" ]] || echo -n "$(printf "%+.$2f" $1)"
	fi
}

pp_diff() {
	if [ "$1" == "$2" ];then
		return
	fi
	if [ "$1" == "NaN" ];then
		r=$2
	elif [[ -n "$2" ]];then
		r=$(echo "$1 - $2" | bc 2> /dev/null)
	fi
	[[ -z $r ]] || echo -n "(`pp_diffnumber "$r" $3`)"
}

if [ -z "${JS_HOME}" ]; then
	msg info "Environment variable \$JS_HOME is not exist."
	exit
fi
BENCHMARK_HOME=${JS_HOME}/benchmarks
DATA_HOME=${BENCHMARK_HOME}/data

usage_benchmark_start () {
	cat << EOF
Usage: `basename $0` [-h] [-r] [-t SECONDS] [-s] [-p|-l] LIST
Runs type analysis for every single .js file in the LIST, and records the result.
LIST is a text file containing a list of .js filename. LIST must be newline separated and can use # style comments.

  -h    Display this help and exit.
  -r    Runs without rebuild.
  -t    Set a upper limit time(sec) for each project (default:3600)
  -s    Silent mode for daily report.
  -p	Sparse analysis mode.
  -l    HTML analysis mode
  -m	Prints analysis result(heap).
EOF
	exit

}

update_slocs() {
	set=${BENCHMARK_HOME}/$1
	[ ! -z $1 ] || set=${BENCHMARK_HOME}/"benchmark.list"
	list=`cat $set | grep '^[^#]*' -o`
	msg info_s "* Target: $set"
	analyzer="${JS_HOME}/bin/jsaf"
	
	for v in $list;do
		loc="0"
		if [ "${v:${#v}-4}" == ".htm" ]; then
			if [ "$mode" != "html" ]; then
				tmp="temp.$RANDOM.js"
				$analyzer html -pretty $tmp ${BENCHMARK_HOME}/$v 2>&1 > /dev/null
				loc=`wc -l $tmp | awk '{ print $1 }'`
				rm $tmp
			fi
		elif [ "${v:${#v}-5}" == ".html" ]; then
			if [ "$mode" != "html" ]; then
				tmp="temp.$RANDOM.js"
				$analyzer html -pretty $tmp ${BENCHMARK_HOME}/$v 2>&1 > /dev/null
				loc=`wc -l $tmp | awk '{ print $1 }'`
				rm $tmp
			fi
		else
			loc=`wc -l $v | awk '{ print $1 }'`
		fi
		echo "${v//\//_} $loc"
	done
}

benchmark_start () {
	timeout=3600 #(sec)
	while getopts ht:rsplm OPT;do
		case "$OPT" in
			h) usage_benchmark_start;;
			t) s_timeout=true;timeout=$OPTARG;;
			r) s_wobuild=true;;
			s) s_silent=true;;
			p) s_sparse=true;;
			l) s_html=true;;
			m) s_memdump='-memdump'
		esac
	done
	shift `expr $OPTIND - 1`

	set=${BENCHMARK_HOME}/$1
	[ ! -z $1 ] || set=${BENCHMARK_HOME}/"benchmark.list"
	list=`cat $set | grep '^[^#]*' -o`
	
	[ -z $s_timeout ] || msg info_s "* Timeout: $timeout(sec)"
	msg info_s "* Target: $set"
	for v in $list;do
		msg info " $v"
	done
	msg info_s "* Analysis option: ${BENCHMARK_OPTION}"
	
	# build
	if [[ -z "$s_wobuild" ]];then
		pushd ${JS_HOME} > /dev/null
		ant compile
		popd > /dev/null
	fi

	declare -i i
	i=0
	date=`date +'%Y-%m-%d'`
	name=${DATA_HOME}/$date
	analyzer="${JS_HOME}/bin/jsaf"

	# create a folder for result data.
	while [ -e $name ];do
		name="${DATA_HOME}/$date.$i"
		i=$i+1
	done
	mkdir -p $name

	# setting information
	commit=`git show --pretty=short | head -n 5`
	echo -e "$commit" > $name/.git_info
	git diff ${JS_HOME}/src/* > $name/.git_diff
	${BENCHMARK_HOME}/bin/machine_info > $name/.machine_info
	old=$(cat ${BENCHMARK_HOME}/.recent 2> /dev/null)
	mode=analyze
	[ -z $s_sparse ] || mode=sparse
	[ -z $s_html ] || mode=html
	echo $name > ${BENCHMARK_HOME}/.recent
	echo $timeout > $name/.timeout
	echo ${BENCHMARK_OPTION} > $name/.option
	echo $mode > $name/.mode
	trap ctrl_c INT
	msg info_s "* Analysis mode: $mode"
	time_start=$(date +%s)
	for v in $list;do
		mode_for_this=$mode
		if [ "${v:${#v}-4}" == ".htm" ]; then
			if [ "$mode" != "html" ]; then
				echo "* Analysis mode is changed to 'html'."
				mode_for_this="html"
			fi
		fi
		if [ "${v:${#v}-5}" == ".html" ]; then
			if [ "$mode" != "html" ]; then
				echo "* Analysis mode is changed to 'html'."
				mode_for_this="html"
			fi
		fi
		{
			if [ -z $s_silent ];then
				$analyzer $mode_for_this ${BENCHMARK_OPTION} ${s_memdump} -statdump ${BENCHMARK_HOME}/$v 2>&1 | tee $name/${v//\//_}.out
			else
			echo -n "* Analyzing '${v}' : "
				$analyzer $mode_for_this ${BENCHMARK_OPTION} ${s_memdump} -statdump ${BENCHMARK_HOME}/$v &> $name/${v//\//_}.out
			fi

			echo $JAVA_FLAGS >> $name/.java_flag.time
			exit
		} & pid=$!
		timer $timeout $pid & pid_timer=$!
		wait 2> /dev/null
		RETVAL=$?

		if [ $RETVAL -ne 0 ];then
			killtree $pid_timer 9
			wait 2> /dev/null
			killtree $pid 9
			wait 2> /dev/null
			confirm "Continue to analyze?"
			[ $? -eq 1 ] || break
		else
			echo $v >> $name/.time.finished
		fi
	done
	time_finish=$(date +%s)
	msg info_s "* Total analysis time(s): $((time_finish - time_start))" | tee $name/.time_info
	msg info_s "* Git revision information"
	msg info_s "=========================="
	echo -e "$commit"
	
	msg info_s "Finished: $name"
	if [ ! -z $s_silent ];then
		shift
		diff=$old
		info=1
		show_table
	fi
}

usage_show_table () {
	cat << EOF
Usage: `basename $0` [-h] [-d TARGET_RESULT] [-t] [-i] [RESULT]
Show the number table for RESULT. Show the most recent RESULT if a RESULT is omitted.

  -h    Display this help and exit.
  -d    Show the difference between TARGET_RESULT and RESULT on the RESULT.
  -t	Show a TAJS time column(hard-coded data).
  -i    Show the detailed project information.
EOF
	exit
}

show_table () {
	while getopts ithd: OPT;do
		case "$OPT" in
			i) info=1;;
			t)
				tajs=1
				tajs_time=${BENCHMARK_HOME}/tajs.data
				while read key value;do
					declare -A tajs_tbl[$key]=$value
				done < $tajs_time
				;;
			d)
				diff=${OPTARG%/}
				[ -e $diff ] || diff=$DATA_HOME/$diff
				echo "Set diff target: $diff"
				;;
			h) usage_show_table;;
		esac
	done
	shift `expr $OPTIND - 1`

	if [ -z "$1" ];then
		dv=${BENCHMARK_HOME}/.recent
		if [ -f ${dv} ];then
			target=`cat ${dv}`
		else
			usage_show_table
		fi
	else
		target=${1%/}
	fi

	if [ ! -e $target ];then
		target=$DATA_HOME/$target
	fi
	if [ ! -e $target ];then
		msg warn_s "Testset '`basename $target`' doesn't exist."
		exit
	fi
	s_diff=''
	[[ ! -e "$diff" ]] || s_diff="(diff ${diff##*/})"
	msg info_s "* Benchmark result for `basename $target` $s_diff"
	
	if [ -n "$info" ]; then
		if [ -e $target/.git_info ];then
			cat $target/.git_info
			[[ ! -e "$diff" ]] || echo "diff $(cat $diff/.git_info 2>/dev/null)"
			echo
		fi
		if [ -e $target/.machine_info ];then
			msg text "* Machine information"
			msg text "====================="
			cat $target/.machine_info
			echo
		fi
		if [ -e $target/.java_flag ];then
			flag=`cat $target/.java_flag`
			msg text "* Java flag: $flag"
		fi
	else
		if [ -e $target/.git_info ];then
			cat $target/.git_info | head -n 1
			[[ ! -e "$diff" ]] || echo "(diff $(cat $diff/.git_info 2>/dev/null | head -n 1))"
		fi
	fi
	if [ -e $target/.mode ];then
		read mode < $target/.mode	
		[ "$mode" == "sparse" ] && s_sparse=1
		echo -n "Analysis mode: $mode"
		temp=''
		[[ -e "$diff" ]] && [[ -e $diff/.mode ]] && temp="($(cat $diff/.mode 2>/dev/null))"
		echo $temp
	fi
	if [ -e $target/.option ];then
		echo "Analysis option: $(cat $target/.option)"
		[[ -e "$diff" ]] && [[ -e $diff/.option ]] && echo "diff options: $(cat $diff/.option 2>/dev/null)"
	fi
	echo

	list=`ls -1 $target/*.out | sed 's/.out$//g' | uniq`
	head_tajs=''
	head_sparse=''
	[[ ! -n "$tajs" ]] || head_tajs='TAJS(s),'
	[[ ! -n "$s_sparse" ]] || head_sparse='EDGE(s),'
	items=(
		"# active basic block(#): "
		"# Fixpoint iteration(#): "
		"# definite deref. ratio(%): "
		"# definite type ratio(%): "
		"# has undefined ratio(%): "
		"# constant access ratio(%): "
		"# Time for front end(s): "
		"# Time for analysis(s): "
		"# Total exception(#): "
		"# dead instructions(#): "
		"# Result heap memory(mb): "
		"# edge recovering time: "
		"# Basic block(#): "
		)
	(echo "name,LINE#,BB#,ABBE#,iter#,deref(%),type(%),udf(%),access(%),ftime(s),atime(s),${head_sparse}${head_tajs}exc(#),dead(I#),mem(mb),warn#,err,eval";
	(for v in $list;do
		n=${v##*/}
		filter=`cat $v.out | grep "\(^# .*\)\|\(^\* .*\)\|\(Exception\)"`
		filter2=''
		if [[ -f "$diff/$n.out" ]];then
			filter2=`cat $diff/$n.out | grep "\(^# .*\)\|\(^\* .*\)\|\(Exception\)"`
		fi
		idx=0
		while [ "$idx" -lt "${#items[@]}" ];do
			t=$(echo -e "$filter" | grep -m 1 "${items[$idx]}")
			t=${t:${#items[$idx]}}
			result[$idx]=$t
			if [[ -n "$filter2" ]];then
				t=$(echo -e "$filter2" | grep -m 1 "${items[$idx]}")
				t=${t:${#items[$idx]}}
				result2[$idx]=$t
			fi
			let "idx=$idx+1"
		done
		fpath=${BENCHMARK_HOME}/$(cat ${BENCHMARK_HOME}/"benchmark.list" | grep -m 1 "${n}")
		fpath=`expr "$fpath" : '\(.*\)'`
		sloc=`grep "$n" ${BENCHMARK_HOME}/locs | awk '{ print $2 }'`
#		sloc=`cat $fpath | wc -l | sed 's/ //g'`
		warn=`echo -e "$filter" | grep "* Warning" | sort | uniq | wc -l | sed 's/ //g'`
		[[ -z "$filter2" ]] || warn2=`echo -e "$filter2" | grep "* Warning" | sort | uniq | wc -l | sed 's/ //g'`
		err=`echo -e "$filter" | grep "\(OutOfMemory\)\|\(RegularExpression\)" -o | head -n 1`
		evale=`echo -e "$filter" | grep "kr.ac.kaist.jsaf.nodes.IREval" -o | head -n 1`
		bottxt=`grep "** Dump Exit" -A 2 $v.out | tail -n 1`
		bot=""
		if [[ $bottxt == "    Bot" ]]; then
			bot="(Bot)"
		fi

		echo -n "${n//,/.}$bot"
		echo -n ",`pp_number ${sloc}`"
		echo -n ",`pp_number "${result[12]}"``pp_diff "${result[12]}" "${result2[12]}"`"
		echo -n ",`pp_number "${result[0]}"``pp_diff "${result[0]}" "${result2[0]}"`"
		echo -n ",`pp_number "${result[1]}"``pp_diff "${result[1]}" "${result2[1]}"`"
		echo -n ",`pp_number "${result[2]}" 1``pp_diff "${result[2]}" "${result2[2]}" 1`"
		echo -n ",`pp_number "${result[3]}" 1``pp_diff "${result[3]}" "${result2[3]}" 1`"
		echo -n ",`pp_number "${result[4]}" 1``pp_diff "${result[4]}" "${result2[4]}" 1`"
		echo -n ",`pp_number "${result[5]}" 1``pp_diff "${result[5]}" "${result2[5]}" 1`"
		echo -n ",`pp_number "${result[6]}" 2``pp_diff "${result[6]}" "${result2[6]}" 2`"
		echo -n ",`pp_number "${result[7]}" 2``pp_diff "${result[7]}" "${result2[7]}" 2`"
		[[ ! -n "$s_sparse" ]] || echo -n ",`pp_number "${result[11]}" 2``pp_diff "${result[11]}" "${result2[11]}" 2`"
		[[ ! -n "$tajs" ]] || echo -n ", `pp_number "${tajs_tbl[${n}]}" 2`"
		echo -n ",`pp_number "${result[8]}"``pp_diff "${result[8]}" "${result2[8]}"`"
		echo -n ",`pp_number "${result[9]}"``pp_diff "${result[9]}" "${result2[9]}"`"
		echo -n ",`pp_number "${result[10]}" 0``pp_diff "${result[10]}" "${result2[10]}" 0`"
		echo -n ",`pp_number "${warn}"``pp_diff "${warn}" "${warn2}"`"
		echo -n ",`pp_number ${err}`"
		echo -n ",`pp_number ${evale##*.}`"
		echo ""
	done) | sort -t , -gk 11) | column -t -s , | colorize
}

cmd=`basename $0`

case $cmd in
	"benchmark_start" | "show_table" | "diff_table" | "update_slocs" )
		$cmd $@;;
	*) exit;;
esac

