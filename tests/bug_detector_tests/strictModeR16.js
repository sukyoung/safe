"use strict";

// [R16]
with({});

function notStrict() {
    // [R16]
    with({});
}

notStrict();
