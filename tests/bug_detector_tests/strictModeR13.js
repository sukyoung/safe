"use strict";

var x_R13 = 10;
delete x_R13;

function notStrict() {
    delete x_R13;
}

notStrict();
