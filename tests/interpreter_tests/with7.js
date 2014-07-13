z = 8
function f() {
  var x = 5
  var y = 42
  with ({y : 3}) {
    z = 38
    _<>_print(x)
    _<>_print(y)
    _<>_print(z)
  }
}
f()
