"use strict";

// [R4-4]
var x_R4 = {};
Object.freeze(x_R4);
x_R4.p = 1;

function notStrict() {
    // [R4-4]
    var y_R4 = {};
    Object.freeze(y_R4);
    y_R4.p = 1;
}

notStrict();
