"use strict";

function strict1() {
    "use strict";
    arguments.caller;
}

function strict2() {
    "use strict";
    arguments.callee;
}

function notStrict1() {
    arguments.caller;
}

function notStrict2() {
    arguments.callee;
}

strict1();
strict2();
notStrict1();
notStrict2();
