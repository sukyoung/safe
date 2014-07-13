var temp = 10;
function f() {temp++; return temp;}

try {
  y + f();
} catch (e) {}

temp
