"use strict";

// [R17]
try {} catch(eval) {}
try {} catch(arguments) {}

function notStrict() {
    // [R17]
    try {} catch(eval) {}
    try {} catch(arguments) {}
}

notStrict();
