"use strict";

// [R4-1]
x_R4 = 10;

function notStrict() {
    // [R4-1]
    y_R4 = 10;
}

notStrict();
