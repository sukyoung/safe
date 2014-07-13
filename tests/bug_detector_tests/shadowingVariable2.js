function f(t, b, s) {
  var g = t;
  function g() {
    function h() {return -1;}
    return h();
  }
  return g;
}
f(1,2,3);
