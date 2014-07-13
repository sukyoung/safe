function x(a,b,c) {_<>_print(this.z + ", " + a + ", " + b + ", " + c);}
x.call({});
x.call({}, 1);
x.call({}, 1, 2);
x.call({}, 1, 2, 3);
x.call({z: 42}, 1, 2, 3);

"PASS"
