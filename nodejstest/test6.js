// recommended command
// safe analyze -nodejs -analyzer:exitDump -heapBuilder:jsModel -heapBuilder:callsiteSensitivity=2 test6.js

var x = require("./test6_submodule1.js");
result1 = (x==require); // false
result2 = (x.cache == require.cache); // true
