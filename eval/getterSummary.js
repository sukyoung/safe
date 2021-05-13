const fs = require('fs');

// argurments
let dirname = 'result_gs';

const total = [], pass = [], fail = [];

fs.readdirSync(dirname).forEach(name => {
  if (!name.startsWith('test')) return;
  id = Number(name.substring(4));
  total.push(id);
  base = `${dirname}/${name}`;

  const contents = fs.readFileSync(`${base}/result`).toString();
  if(contents.endsWith("PASS\n")){
    pass.push(id);
  } else {
    fail.push(id);
  }
});
console.log(total.length);
console.log(pass.length);
console.log(fail.length);
console.log(fail.join("\n"));
