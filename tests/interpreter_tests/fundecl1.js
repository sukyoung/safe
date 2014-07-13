var x = 1;
function f() { var x = 2;
               function g() { x = 3; }
               g();
               _<>_print(x);
}
f()
