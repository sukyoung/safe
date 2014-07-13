var x = 'global';
_<>_print('x in '+x);
window = this;
f = function() { var x = 'function'; _<>_print('x in '+x); window.eval("_<>_print('x in '+x)"); }
f();
"PASS"
