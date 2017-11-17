x = {a : 3};
var smalloc = process.binding('smalloc');
smalloc.alloc(x, 5); // x = {0 : 0, 1 : 0, 2 : 0, 3 : 0, 4 : 0, a : 3}
smalloc.truncate(x, 4); // x = {0 : 0, 1 : 0, 2 : 0, 3 : 0, a : 3}

