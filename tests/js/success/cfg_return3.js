var x;
function f() {
  var y = function g(x) {
    var temp = 1;
    if (y) return;
    else {
      var z = 678;
      print("in");
      var w = function h(t) {
        return temp;
      }
      w(3);
    }
  }
}

f();
