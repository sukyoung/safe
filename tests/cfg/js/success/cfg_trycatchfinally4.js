var x = 1;
var obj = {a:"A", b:"B"};
try {
  if (obj.a == x) delete obj.b;
  else {
    while (true) {
      if (x--) break;
    }
  }
  throw x;
} catch (e) {
  var value = function() {
    return e;
  }
  var arr = [4, 2, test()];
  undefined;
}
