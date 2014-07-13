var f = 1, g = 2, h = 3;
function f(g, h) {
  function g(h) {
    var f = 3;
    function h() {
      return f;
    }; return h();
  }; return g(g);
};f(g, h);
