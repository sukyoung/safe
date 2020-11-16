const fs = require('fs');

// helpers
let content = '';
function toArray(args) {
  let arr = [];
  for (let i = 0; i < args.length; i++) arr.push(args[i]);
  return arr;
}
function getPath(filename) { return base + '/' + filename; }
function add() { content += toArray(arguments).map(x => x + '\t').join(); }
function newline() { content += '\n'; }
function read(filename) { return fs.readFileSync(getPath(filename)).toString(); }
function readLines(filename) { return read(filename).split('\n'); }
function isTimeout() {
  let safe = read(ds_mode ? 'ds-safe.result' : 'safe.result');
  return safe.includes('Timeout');
}
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

fs.readdirSync(dirname).forEach(name => {
  if (!name.startsWith('test')) return;
  if (excluded.total.has(name)) return;
  total++;
  id = Number(name.substring(4));
  base = `${dirname}/${name}`;
  add(id);

  if (!exists('diff.result')) {
    // timeout case
    if (isTimeout()) { add('T'); timeout.push(id); }
    // error case
    else { add('E'); error.push(id); }
    newline();
    return;
  }

  let lines = readLines('diff.result');
  let [miss, over] = lines.filter(line => line.includes('safe'));
  miss = Number(miss.split(': ')[1]);
  over = Number(over.split(': ')[1]);

  // unsound case
  if (miss) { add('U'); unsound.push(id); newline(); return; }

  // sound case
  add('S'); sound.push(id);

  newline();
});

// summary.tsv
write('summary.tsv', content);

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
