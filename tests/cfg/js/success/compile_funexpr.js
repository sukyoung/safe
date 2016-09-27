function f(a, b) {
  var z = function g(c) { false; this; c; }
  return z;
}
var x = function (x) { return x; }
y = (function h(n) { if (n == 0) return n; else return h(n-1); })(3)
