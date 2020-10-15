const fs = require('fs');
var filename = process.argv[2];
var dirname = process.argv[3];
if (!filename || !dirname) {
  console.log('Usage: node cut.js <filename> <dirname>');
  return;
}

var text = fs.readFileSync(filename, 'utf8');
var lines = text.split('\n');

function checkLine(str) {
  return str.startsWith("/*----") && str.endsWith("---*/");
}

function save(filename, content) {
  fs.writeFileSync(filename, content, 'utf8');
}

(() => {
  let n = 1, content = '';
  for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    if (checkLine(line)) {
      save(`${dirname}/test${n++}.js`, content.trim());
      content = '';
    } else {
      content += line + '\n';
    }
  }
})();
