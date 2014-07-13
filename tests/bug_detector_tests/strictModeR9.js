"use strict";

// [R9]
var x_R9 = {param1: 1, param1: 2};

function notStrict() {
    // [R9]
    var y_R9 = {param1: 1, param1: 2};
}

notStrict();
