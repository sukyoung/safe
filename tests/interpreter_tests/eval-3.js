var x = 'global';
_<>_print('x in '+x);
f = function() { var x = 'function'; _<>_print('x in '+x); ev = eval; ev("_<>_print('x in '+x)"); }
f();
"PASS"
