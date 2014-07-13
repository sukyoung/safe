var x = 4;
var y = false;
var z = (1,2,3,4);
if (y) {  // should print line 4 ~ 7 as unreachable code.
  var f = function () { // bugDetector reports this as 'anonymous_function'. Better print 'f'.
    return x;
  }
  f();
} else if (x < 2) {
  var tmp = 0;
  for (i in z) {
    tmp += i;
  }
  tmp + 10;
} else {
  var test = "";
}
