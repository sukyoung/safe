import os
import sys

load_line_no = 0

def merge(files, newfile):
	global load_line_no

	total = 0
	last = 0

	out = open(newfile, "w")
	for name in files:
		f = open(name, "r")
		lines = f.readlines()
		total += len(lines)
		last = len(lines)
		out.writelines(lines)
		f.close()
	out.close()

	load_line_no = total - last

def check_file(jsfile):
	print("testing " + jsfile)

	merged_file = "merged.js"
	#merge(["setting.js", "lodash.js", "test-setting.js", jsfile], merged_file)
	merge(["setting.js", "lodash.js", jsfile], merged_file)

	jalangi_cmd = "node ../../../../jalangi2/src/js/commands/jalangi.js --inlineIID --inlineSource --analysis ../../../../jalangi2/src/js/sample_analyses/ChainedAnalyses.js --analysis ../../../../jalangi2/src/js/sample_analyses/pldi16/BranchCoverage.js "+ merged_file
	safe_cmd = "../../../bin/safe bugDetect " + merged_file

	jalangi_alarms = os.popen(jalangi_cmd).readlines()
	safe_alarms = os.popen(safe_cmd).readlines()[:-1]
	total = safe_alarms.pop()
	total = int(total.split("=")[1].split()[0])
	
	def parse_jalangi_alarm(alarm):
		taken = alarm.split()[0]
		splits = alarm.split(":")
		line = int(splits[1])
		st = int(splits[2])
		fn = int(splits[4].split(")")[0])
		return (line, st, fn, taken)
	
	def parse_safe_alarm(alarm):
		taken = alarm.split(" ==> ")[1].strip()
		splits = alarm.split()[0].split(":")
		line = int(splits[1])
		if len(splits) == 4:
			st = int(splits[2].split("-")[0])
			fn = int(splits[3])
		elif "-" in splits[2].split()[0]:
			st = int(splits[2].split("-")[0])
			fn = int(splits[2].split("-")[1])
		else:
			st = int(splits[2].split()[0])
			fn = st
		return (line, st, fn, taken)

	jalangi_alarms = sorted(list(map(parse_jalangi_alarm, jalangi_alarms)))
	safe_alarms = sorted(list(map(parse_safe_alarm, safe_alarms)))

	## ignore alarms from loading
	#jalangi_alarms = list(filter(lambda x: x[0] > load_line_no, jalangi_alarms))
	#safe_alarms = list(filter(lambda x: x[0] > load_line_no, safe_alarms))

	print("jalangi alarms:")
	print(jalangi_alarms)
	print("%d / %d"%(len(jalangi_alarms), total))
	print("safe alarms:")
	print(safe_alarms)
	print("%d / %d"%(len(safe_alarms), total))

	
	print("alarms that safe miss (There shouldn't be any):")
	for a in jalangi_alarms:
		if not a in safe_alarms:
			print(a)
		
	print("alarms that safe overapproximate:")
	for a in safe_alarms:
		if not a in jalangi_alarms:
			print(a)

def test():
	pass

if __name__ == "__main__":
	if len(sys.argv) < 3 or sys.argv[1] not in ["-f", "-d", "-t"]:
		print("usage:")
		print(" python3 branch_compare.py -f [file_name]")
		print(" python3 branch_compare.py -d [directory_name]")
		print(" python3 branch_compare.py -t")
		sys.exit(0)
	if sys.argv[1] == "-f":
		check_file(sys.argv[2])
	elif sys.argv[1] == "-d":
		directory = sys.argv[2]
		files = os.listdir(directory)
		for f in files:
			splits = f.split(".")
			if len(splits) > 1 and splits[-1] == "js":
				check_file(os.path.join(directory, f))
	else:
		test()
