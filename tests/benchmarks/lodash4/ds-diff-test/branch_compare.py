import os
import sys
import json

def check_file():
  f = open("diff.result", "w")
  def out(data):
    f.write(str(data) + "\n")

  lf = open("log", "w")
  def log(data):
    lf.write(str(data) + "\n")


  def spanNotFound(alarm, span):
    log("Span " + alarm + " @ " + str(span) + " not in rev_info")

  merged_name = "merged"
  filename = merged_name + ".js"

  # Load analysis result
  jalangi_alarms = open("jalangi.result", "r").readlines()
  jalangi_alarms = filter(lambda l: "branch taken at" in l, jalangi_alarms)
 
  ds_jalangi_alarms = open("ds-jalangi.result", "r").readlines()
  ds_jalangi_alarms = filter(lambda l: "branch taken at" in l, ds_jalangi_alarms)
  
  ds_safe_alarms = open("ds-safe.result", "r").readlines()
  ds_safe_alarms = filter(lambda l: l.startswith(filename), ds_safe_alarms)
  
  safe_alarms = open("safe.result", "r").readlines()
  safe_alarms = filter(lambda l: l.startswith(filename), safe_alarms)

  # Gather span info
  info = json.load(open(merged_name + "_jalangi_.json"))

  def jalangi_filter(p):
    if not p[0].isdigit():
      return False
    if len(p[1]) < 5:
      return False
    kind = p[1][4]
    return (kind == 'C' or kind == 'C2')

  info = dict(filter(jalangi_filter, info.items()))
  rev_info = dict(map(lambda p: (tuple(p[1][:-1]),p[0]), info.items()))

  # Gather span by lines
  ranges_by_line = {}
  for span in rev_info:
    line = span[0]
    if line in ranges_by_line:
      ranges_by_line[line].append(span)
    else:
      ranges_by_line[line] = [span]

  def parse_jalangi_alarm(alarm):
    taken = alarm.split()[0] == "True"
    splits = alarm.split(":")
    l1 = int(splits[1])
    c1 = int(splits[2])
    l2 = int(splits[3])
    c2 = int(splits[4].split(")")[0])

    span = (l1, c1, l2, c2)

    if not (span in rev_info):
      spanNotFound(alarm, span)
    assert(span in rev_info)

    return (span, taken)

  def parse_safe_alarm(alarm):
    [pre, taken] = alarm.split(") ==> ")
    taken = taken.strip() == "True"
    ast_len = len(pre) - (pre.find("(") + 1)
    splits = pre.split()[0].split(":")
    l1 = int(splits[1])
    if len(splits) == 4:
      c1 = int(splits[2].split("-")[0])
      l2 = int(splits[2].split("-")[1])
      # c2 = int(splits[3])
    elif "-" in splits[2]:
      c1 = int(splits[2].split("-")[0])
      l2 = l1
      # c2 = int(splits[2].split("-")[1])
    else:
      c1 = int(splits[2])
      l2 = l1
      # c2 = c1
    c2 = c1 + ast_len

    span = False

    rngs = ranges_by_line[l1]
    min_dist = -1
    for (_, s, l, e) in rngs:
      if l != l2:
        continue
      dist = abs(s - c1) + abs(e - c2)
      if min_dist == -1 or min_dist > dist:
        min_dist = dist
        span = (l1, s, l2, e)

    if span == False:
      spanNotFound(alarm, (l1, c1, l2, c2))
      raise Error("Not found")

    return (span, taken)

  # Parse
  jalangi_alarms = sorted(list(map(parse_jalangi_alarm, jalangi_alarms)))
  ds_jalangi_alarms = sorted(list(map(parse_jalangi_alarm, ds_jalangi_alarms)))
  ds_safe_alarms = sorted(list(map(parse_safe_alarm, ds_safe_alarms)))
  ds_alarms = ds_jalangi_alarms + ds_safe_alarms
  safe_alarms = sorted(list(map(parse_safe_alarm, safe_alarms)))

  out("alarms that ds miss (There shouldn't be any):")
  for a in jalangi_alarms:
    if not a in ds_alarms:
      out(a)

  out("alarms that safe miss (There shouldn't be any):")
  for a in ds_alarms:
    if not a in safe_alarms:
      out(a)

  out("alarms that ds overapproximate jalangi:")
  for a in ds_alarms:
    if not a in jalangi_alarms:
      out(a)

  out("alarms that safe more overapproximate than ds:")
  for a in safe_alarms:
    if not a in ds_alarms:
      out(a)

  #out("alarms that differ:")
  #for a in ds_alarms:
  #  b = (a[0], not a[1])
  #  if b in ds_alarms:
  #    continue
  #  if b in safe_alarms:
  #    out(a)
  f.close()
  lf.close()

def test():
  pass

if __name__ == "__main__":
  check_file()
