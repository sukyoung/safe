"use strict";

// [R18]
function x_R18(eval, arguments) {}

function notStrict() {
    // [R18]
    function y_R18(eval, arguments) {}
}

notStrict();
