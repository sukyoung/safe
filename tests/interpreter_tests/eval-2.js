var x = 'global';
_<>_print('x in '+x);
f = function() { var x = 'function'; _<>_print('x in '+x); eval("_<>_print('x in '+x)"); }
f();
"PASS"
