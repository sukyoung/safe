import os
import sys
import json

def check_file():
  f = open("diff.result", "w")
  def out(data):
    f.write(str(data) + "\n")

  merged_name = "merged"
  filename = merged_name + ".js"

  # Load analysis result
  jalangi_alarms = open("jalangi.result", "r").readlines()
  safe_alarms = open("safe.result", "r").readlines()
  total = list(filter(lambda l: l.startswith("total"), safe_alarms))[0]
  total = int(total.split("=")[1].split()[0])
  safe_alarms = filter(lambda l: l.startswith(filename), safe_alarms)

  # Gather span info
  info = json.load(open(merged_name + "_jalangi_.json"))
  info = dict(filter(lambda p: p[0].isdigit() and len(p[1]) >=5 and p[1][4] == 'C', info.items()))
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
      raise Error("Not found")

    return (span, taken)

  # Parse
  jalangi_alarms = sorted(list(map(parse_jalangi_alarm, jalangi_alarms)))
  safe_alarms = sorted(list(map(parse_safe_alarm, safe_alarms)))

  out("alarms that safe miss (There shouldn't be any):")
  for a in jalangi_alarms:
    if not a in safe_alarms:
      out(a)

  out("alarms that safe overapproximate:")
  for a in safe_alarms:
    if not a in jalangi_alarms:
      out(a)

  out("alarms that differ:")
  for a in jalangi_alarms:
    b = (a[0], not a[1])
    if b in jalangi_alarms:
      continue
    if b in safe_alarms:
      out(a)
  f.close()

def test():
  pass

if __name__ == "__main__":
  check_file()
