// recommended command
// safe analyze -nodejs -analyzer:exitDump -heapBuilder:jsModel -heapBuilder:callsiteSensitivity=2 test2.js

x = require("./test2_submodule1.js"); 
x = require("./test2_submodule2.js"); //x=5, y = 2; 
