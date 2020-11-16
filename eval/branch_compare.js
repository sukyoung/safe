const fs = require('fs');

// helpers
var content = '';
function println(str) { content += str + '\n'; }
function spanNotFound(alarm, span) {
  console.warn(`Span ${alarm} @ ${span} not in info`);
}
function read(filename) { return fs.readFileSync(filename).toString(); }
function readLines(filename) { return read(filename).split('\n'); }
function readJson(filename) { return JSON.parse(read(filename)); }
function write(filename) { fs.writeFileSync(filename, content); }
function is_numeric(str) { return /^\d+$/.test(str); }
function info_filter(info) {
  let out = {};
  for (let prop in info) {
    let value = info[prop];
    if (is_numeric(prop) && value.length === 5) {
      let kind = value[4];
      if (kind == 'C' || kind == 'C2') {
        let span = new Span(value[0], value[1], value[2], value[3]);
        out[span] = prop;
      }
    }
  }
  return out;
}
function Loc(l, c) {
  this.line = l;
  this.col = c;
}
function Span(sl, sc, el, ec) {
  this.start = new Loc(sl, sc);
  this.end = new Loc(el, ec);
};
Span.prototype.toString = function toString() {
  return `${this.start.line}:${this.start.col}:${this.end.line}:${this.end.col}`;
}
Span.from = function from(str) {
  let arr = str.split(':');
  return new Span(arr[0], arr[1], arr[2], arr[3]);
}
function get_line(alarm) {
  return Number(alarm.split(':')[0]) * 1000 + Number(alarm.split(':')[1]);
}
function alarm_sort(alarms) {
  return alarms.sort((x, y) => get_line(x) - get_line(y));
}

// for jalangi
function jalangi_filter(msg) { return msg.includes('branch taken at'); }
function parse_jalangi_alarm(alarm) {
  let taken = alarm.split(' ')[0] == 'True';
  let arr = alarm.split(':');
  let span = new Span(arr[1], arr[2], arr[3], arr[4].split(')')[0]);
  if (!(span in info)) spanNotFound(alarm, span);
  return `${span}-${taken}`;
}
function read_jalangi(filename) {
  return readLines(filename).filter(jalangi_filter).map(parse_jalangi_alarm);
}

// for safe
function safe_filter(msg) { return msg.startsWith('target.js:'); }
function parse_safe_alarm(alarm) {
  let [pre, taken] = alarm.split(') ==> ');
  taken = taken.trim() == 'True';
  let ast_len = pre.length - (pre.indexOf('(') + 1)
  let arr = pre.split(' ')[0].split(':');

  var l1 = arr[1];
  if (arr.length === 4) {
    var [c1, l2] = arr[2].split('-'), c2 = arr[3];
  } else if (arr[2].includes('-')) {
    var [c1, c2] = arr[2].split('-'), l2 = l1;
  } else {
    var c1 = arr[2], l2 = l1, c2 = c1;
  }
  c2 = Number(c1) + ast_len;

  let found = undefined;

  rngs = rngs_by_line[l1] || [];
  let min_dist = -1;
  for (let span of rngs) {
    let s = span.start.col;
    let l = span.end.line;
    let e = span.end.col;
    if (l !== l2) continue;
    let dist = Math.abs(s - c1) + Math.abs(e - c2);
    if (min_dist == -1 || min_dist > dist) {
      min_dist = dist;
      found = new Span(l1, s, l2, e);
    }
  }

  if (!found) spanNotFound(alarm, new Span(l1, c1, l2, c2));

  return `${found}-${taken}`;
}
function read_safe(filename) {
  return alarm_sort(readLines(filename)
    .filter(safe_filter)
    .map(parse_safe_alarm)
  );
}

// check results
function check() {
  let safe_result = ds_mode ? 'ds-safe.result' : 'safe.result';
  if (!read(safe_result).includes("[BugDetect] END")) return false;

  // read info
  info = info_filter(readJson('target_jalangi_.json'));

  // gather span by lines
  rngs_by_line = {};
  for (let key in info) {
    let span = Span.from(key);
    let line = span.start.line;
    if (line in rngs_by_line) rngs_by_line[line].push(span);
    else rngs_by_line[line] = [span];
  }

  // reaad alarms
  jalangi_alarms = read_jalangi('jalangi.result');
  jalangi_alarms = alarm_sort(Array.from(new Set([...jalangi_alarms])));

  safe_alarms = [];
  if (ds_mode) safe_alarms = safe_alarms.concat(read_jalangi('ds-jalangi.result'));
  safe_alarms = safe_alarms.concat(read_safe(safe_result));
  safe_alarms = alarm_sort(Array.from(new Set([...safe_alarms])));

  // branch coverage
  println(`JALANGI / SAFE = ${jalangi_alarms.length} / ${safe_alarms.length}`);

  // unsound
  unsound_alarms = jalangi_alarms.filter(x => !safe_alarms.includes(x));
  println(`safe miss (jalangi - safe): ${unsound_alarms.length}`);
  unsound_alarms.forEach(x => println(x));

  // over
  over_alarms = safe_alarms.filter(x => !jalangi_alarms.includes(x));
  println(`safe overapproximate (safe - jalangi): ${over_alarms.length}`)
  over_alarms.forEach(x => println(x));

  return true;
}

// ds mode check
var ds_mode = (
  process.argv.includes('-ds') ||
  process.argv.includes('--dynamic-shortcut')
);
if (check()) write('diff.result');
