eval("alert(1)")
function f() {
  eval("alert(2)");
  function eval(x) { alert(0); }
  eval(3)
}
eval("alert(4)")
f()
