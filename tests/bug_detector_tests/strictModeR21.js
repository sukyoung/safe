"use strict";

// [R21]
function eval() {}
function arguments() {}

function notStrict() {
    // [R21]
    function eval() {}
    function arguments() {}
}

notStrict();
