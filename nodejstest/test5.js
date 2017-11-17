// recommended command
// safe analyze -nodejs -analyzer:exitDump -heapBuilder:jsModel -heapBuilder:callsiteSensitivity=2 test5.js

//cylic dependency
main_x = require("./test5_submodule1.js"); // main_x = 3, module1_x = 2, module2_x = 1
console.log(main_x);
console.log(module1_x);
console.log(module2_x);
