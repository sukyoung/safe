import os
import sys
import json

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

	merged_name = "merged"
	merged_file = merged_name + ".js"
	merge(["setting.js", "lodash.js", "test-setting.js", jsfile], merged_file)
	#merge(["setting.js", "lodash.js", jsfile], merged_file)

	jalangi_cmd = "node ../../../../jalangi2/src/js/commands/jalangi.js --inlineIID --inlineSource --analysis ../../../../jalangi2/src/js/sample_analyses/ChainedAnalyses.js --analysis ../../../../jalangi2/src/js/sample_analyses/pldi16/BranchCoverage.js "+ merged_file
	safe_cmd = "../../../bin/safe bugDetect " + merged_file

	# Run analyzers
	jalangi_alarms = os.popen(jalangi_cmd).readlines()
	safe_alarms = os.popen(safe_cmd).readlines()[:-1]
	total = safe_alarms.pop()
	total = int(total.split("=")[1].split()[0])

	# Gather span info
	info = json.load(open(merged_name + "_jalangi_.json"))
	info = dict(filter(lambda p: p[0].isdigit() and len(p[1]) >=5 and p[1][4] == 'C', info.items()))
	rev_info = dict(map(lambda p: (tuple(p[1][:-1]),p[0]), info.items()))

	def parse_jalangi_alarm(alarm):
		taken = alarm.split()[0] == "True"
		splits = alarm.split(":")
		l1 = int(splits[1])
		c1 = int(splits[2])
		l2 = int(splits[3])
		c2 = int(splits[4].split(")")[0])

		span = (l1, c1, l2, c2)

		assert(span in rev_info)

		return (span, taken)
	
	def parse_safe_alarm(alarm):
		taken = alarm.split(" ==> ")[1].strip() == "True"
		splits = alarm.split()[0].split(":")
		l1 = int(splits[1])
		if len(splits) == 4:
			c1 = int(splits[2].split("-")[0])
			l2 = int(splits[2].split("-")[1])
			c2 = int(splits[3])
		elif "-" in splits[2]:
			c1 = int(splits[2].split("-")[0])
			l2 = l1
			c2 = int(splits[2].split("-")[1])
		else:
			c1 = int(splits[2])
			l2 = l1
			c2 = c1

		span = (l1, c1, l2, c2)

		if span not in rev_info:
			newspan1 = (l1, c1, l2, c2 - 1)
			newspan2 = (l1, c1 + 1, l2, c2 - 1)
			if newspan1 in rev_info:
				span = newspan1
			elif newspan2 in rev_info:
				span = newspan2
			else:
				while span not in rev_info:
					l1, c1, l2, c2 = span
					if c1 == c2:
						raise NotImplementedError
					span = (l1, c1, l2, c2-1)

		return (span, taken)

	# Parse
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
	
	print("alarms that differ:")
	for a in jalangi_alarms:
		b = (a[0], not a[1])
		if b in jalangi_alarms:
			continue
		if b in safe_alarms:
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
