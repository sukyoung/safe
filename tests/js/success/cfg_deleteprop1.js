var obj1 = {x:"A", y:"B"};
var obj2 = {a:"C", b:"D", c:3, d:obj1};
delete obj2.b;
delete obj2.d.x;
