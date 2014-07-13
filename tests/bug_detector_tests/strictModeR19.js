"use strict";

// [R19]
function x_R19(param2, param2) {}

function notStrict() {
    // [R19]
    function y_R19(param2, param2) {}
}

notStrict();
