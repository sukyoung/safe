"use strict";

// [R10]
var x_R10_1 = {set test1(eval) {}, set test2(arguments) {}};
var x_R10_2 = {eval: 1, arguments: 2};
var x_R10_3 = {"eval": 3, "arguments": 4};

function notStrict() {
    // [R10]
    var y_R10_1 = {set test1(eval) {}, set test2(arguments) {}};
    var y_R10_2 = {eval: 1, arguments: 2};
    var y_R10_3 = {"eval": 3, "arguments": 4};
}

notStrict();
