x = {a : 3};
var smalloc = process.binding('smalloc');
smalloc.alloc(x, 5); // x = {0 : 0, 1 : 0, 2 : 0, 3 : 0, 4 : 0, a : 3}
x[0]=1;
x[1]=2;
x[2]=3;
x[3]=4;
x[4]=5;
y = {};
smalloc.sliceOnto(x, y, 1, 3); // y = {0 : 2, 1 : 3}

