x = {a : 3};
var smalloc = process.binding('smalloc');
smalloc.alloc(x, 3); // x = {0 : 0, 1 : 0, 2 : 0, a : 3}
