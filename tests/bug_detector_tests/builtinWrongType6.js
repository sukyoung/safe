Object.getPrototypeOf(123);
//if(Math.random()) Array.prototype.sort(567);
Function.prototype.apply(1, 2);

function f() {}
f();
if(Math.random()) t = 1; else t = f;
f.apply(null, t);
