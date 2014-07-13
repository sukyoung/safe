function f() {
  var x = 0;
  var c = function() {

    return ++x;
  };
  x = 100;
  return c;
};
var g = f();
g();
g();
g();
