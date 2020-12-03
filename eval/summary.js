const fs = require('fs');

Set.prototype.union = function(setB) {
    var union = new Set(this);
    for (var elem of setB) {
        union.add(elem);
    }
    return union;
}

// helpers
let cur = [];
let content = [cur];
function toArray(args) {
  let arr = [];
  for (let i = 0; i < args.length; i++) arr.push(args[i]);
  return arr;
}
function getPath(filename) { return base + '/' + filename; }
function add() { toArray(arguments).forEach(x => cur.push(x)); }
function newline() { cur = []; content.push(cur); }
function read(filename) { return fs.readFileSync(getPath(filename)).toString(); }
function readLines(filename) { return read(filename).split('\n'); }
function isTimeout() { return read(SAFE).includes('Timeout'); }
function exists(filename) { return fs.existsSync(getPath(filename)); }
function write(filename, data) {
  return fs.writeFileSync(`${dirname}/${filename}`, data);
}
function writeJson(filename, data) {
  return write(filename, JSON.stringify(data, null, 2));
}
function sort(arr) { return arr.sort((x, y) => x - y); }

const excluded = (() => {
  const SAFE_HOME = process.env.SAFE_HOME;
  const filename = `${SAFE_HOME}/tests/benchmarks/lodash4/excluded.json`;
  const excluded = JSON.parse(fs.readFileSync(filename).toString());
  excluded.total = new Set([...excluded.eval, ...excluded.proto, ...excluded.implicit_call]);
  return excluded;
})();

// total counts
var total = 0;
var sound = [];
var error = [];
var timeout = [];
var unsound = [];

// argurments
let dirname = 'result';
let argv = Array.from(process.argv);
argv.shift();
argv.shift();
argv.forEach(arg => {
  if (!arg.startsWith('-')) dirname = arg;
})
let ds_mode = (
  process.argv.includes('-ds') ||
  process.argv.includes('--dynamic-shortcut')
);
let SAFE = ds_mode ? 'ds-safe.result' : 'safe.result';
let DIFF = 'diff.result';
let DS = 'ds-jalangi.result';

fs.readdirSync(dirname).forEach(name => {
  if (!name.startsWith('test')) return;
  if (excluded.total.has(name)) return;
  total++;
  id = Number(name.substring(4));
  base = `${dirname}/${name}`;
  add(id);

  if (!exists(DIFF)) {
    // timeout case
    if (isTimeout()) { add('T'); timeout.push(id); }
    // error case
    else { add('E'); error.push(id); }
    newline();
    return;
  }

  let [miss, over] = readLines(DIFF).filter(l => l.includes('safe'));
  miss = Number(miss.split(': ')[1]);
  over = Number(over.split(': ')[1]);

  // unsound case
  if (miss) { add('U'); unsound.push(id); newline(); return; }

  // sound case
  add('S'); sound.push(id);

  // time
  let [ds_time, total_time] = readLines(SAFE)
    .filter(l => l.startsWith('TIME: '))[0]
    .match(/\d+/g);

  let ds_state_time;
  let ds_transfer_time;
  if(ds_mode) {
    // ds-analysis-time
    let [sse_time, state_time] = readLines(DS)
      .filter(l => l.startsWith('TIME: '))
      .reduce(([sse, state], l, idx) => {
        const time = +(l.match(/\d+/g)[0]);
        if(idx % 2 === 0) {
          return [sse + time, state];
        } else {
          return [sse, state + time];
        }
      }, [0, 0]);

    add(sse_time);
    let comm_time = ds_time - sse_time;
    add(comm_time);

    ds_transfer_time = comm_time - state_time;
    ds_state_time = state_time;
  } else {
    add(ds_time);
    add("");
  }
  add(total_time);

  // ds count
  let [succ_ds, total_ds] = readLines(SAFE)
    .filter(l => l.includes('COUNT: '))[0]
    .match(/\d+/g);
  add(succ_ds);
  //add(total_ds);

  let safe_fails = readLines(SAFE)
    .filter(l => l.startsWith('FAIL: '))
    .reduce((acc, l) => {
      return acc.add(l.toString().split(": ").pop());
    }, new Set());
  // failed assertion
  if(ds_mode) {
    let ds_jalangi_fails = readLines(DS)
      .filter(l => l.startsWith('FAIL: '))
      .reduce((acc, l) => {
        return acc.add(l.toString().split(": ").pop());
      }, new Set());
    add(safe_fails.union(ds_jalangi_fails).size);
  } else {
    add(safe_fails.size);
  }

  // branch coverage
  let [jalangi_branch, safe_branch] = readLines(DIFF)
    .filter(l => l.includes('JALANGI / SAFE = '))[0]
    .match(/\d+/g);
  if(ds_mode)
    add(jalangi_branch);
  else
    add(safe_branch);

  // ds builtin functions
  if(ds_mode) {
    let ds_jalangi_builtins = readLines(DS)
      .filter(l => l.startsWith('BUILTIN: '))
      .reduce((acc, l) => {
        let id = +l.toString().split(": ").pop();
        if(isNaN(id)) return acc.add(l.substring('BUILTIN: '.length));
        return acc.add(id);
      }, new Set());
    // ds builtin functions
    add([...ds_jalangi_builtins].map((f) => `[${f}]`).join(""));
  } else {
    add("");
  }
  // safe builtin functions
  let safe_builtins = readLines(SAFE)
    .filter(l => l.startsWith('BUILTIN: '))
    .reduce((acc, l) => {
      let id = +l.toString().split(": ").pop();
      if(isNaN(id)) return acc.add(l.substring('BUILTIN: '.length));
      return acc.add(id);
    }, new Set());
  add([...safe_builtins].map((f) => `[${f}]`).join(""));

  if(ds_mode) {
    add(ds_transfer_time);
    add(ds_state_time);
  }
  newline();
});

// summary.tsv

content.sort((x, y) => x[0] - y[0]);

content = [[
  '#', 'status', 'ds-sse-time', 'comm-time', 'total-time', '#DS', '#fail',
  '#branch', 'ds-func', 'abs-func'
]].concat(content);
if(ds_mode) {
  content[0].push(
    'ds-transfer-time',
    'ds-state-time'
  );
}
write('summary.tsv', content.map(l => l.join('\t')).join('\n'));

// result
writeJson('sound.json', sort(sound));
writeJson('error.json', sort(error));
writeJson('timeout.json', sort(timeout));
writeJson('unsound.json', sort(unsound));

// simple_summary
let simple_summary = `SIMPLE RESULT: {
  # TIMEOUT: ${timeout.length}
  # ERROR  : ${error.length}
  # UNSOUND: ${unsound.length}
  # SOUND  : ${sound.length}
  ------------------------------
  # TOTAL  : ${total}
}`;
write('simple_summary', simple_summary);
