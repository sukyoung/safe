"use strict";

// [R4-2]
NaN = 1;
var Infinity = 2;

function strcit() {
    "use strict";
    NaN = 1;
    var Infinity = 2;
}

function notStrict() {
    // [R4-2]
    NaN = 1;
    var Infinity = 2;
}

strcit();
notStrict();
