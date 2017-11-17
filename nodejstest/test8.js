// recommended command
// safe analyze -nodejs -analyzer:exitDump -heapBuilder:jsModel -heapBuilder:callsiteSensitivity=1 test8.js

function g() {

}
function f(k) { 
  x = k; 
  g();
}
f(1);
require("./test8_submodule1.js");
f(2);
// y = UInt
