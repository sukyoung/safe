var test1 = {p1: "123", get p1() {return 3;}};
var test2 = {get p2() {return 3;}, p2: 0};
var test3 = {get p3() {return 3;}, get p3() {return 5;}};
var test4 = {set p4(x) {return 3;}, set p4(y) {return 5;}};
var test5 = {set p1(x) {return 3;}, set p2(y) {return 5;}};
