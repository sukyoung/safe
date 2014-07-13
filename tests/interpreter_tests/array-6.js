var temp = 10;
function f() {temp++; return temp;}

try
{
 var x = [1, y, f()];
}
catch(e) {}

temp;