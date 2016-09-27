var x;
function f(x,y,z) {
  var temp = y + z;
  function g(test) {
    test = test + 6;
    return test - x;
  }
  return g(temp);
};
x = f(1,2,3);
