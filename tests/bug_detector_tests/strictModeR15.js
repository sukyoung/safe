"use strict";

// [R15]
var eval = 1, arguments = 2;

function notStrict() {
    // [R15]
    var eval = 1, arguments = 2;
}

notStrict();
