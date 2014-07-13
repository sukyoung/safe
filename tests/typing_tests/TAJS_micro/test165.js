function aaaAAA(o) {}
function aaaBBB(o) {}
aaaAAA.id = 123;  // for SAFE
aaaBBB.id = "ABC";  // for SAFE

function foo(o) {
    var z1 = "aaa" + o.t;
    //dumpValue(this.aaaAAA);
//    dumpValue(this[z1])
    __result1 = this[z1].id;
    __result2 = this[z1].id;
}
var __expect1 = 123;  // for SAFE
var __expect2 = "ABC";  // for SAFE

function init() {
   foo(A[Math.random()])
   foo(A[Math.random()]);
}

var A = [{t: "AAA"},{t: "BBB"}]
init();
