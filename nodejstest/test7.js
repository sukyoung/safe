// recommended command
// safe analyze -nodejs -analyzer:exitDump -heapBuilder:jsModel -heapBuilder:callsiteSensitivity=2 test7.js

x = 0;
var i=0;
while(i < 3) {
  require("./test7_submodule1.js");
  i++;
}
// x = UInt
