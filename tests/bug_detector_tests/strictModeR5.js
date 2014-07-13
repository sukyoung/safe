"use strict";

// [R5]
eval = 1; eval+= 1; eval++; ++eval;
arguments = 2; arguments+=2; arguments++; ++arguments;

function notStrict() {
    // [R5]
    eval = 1; eval+= 1; eval++; ++eval;
    arguments = 2; arguments+=2; arguments++; ++arguments;
}

notStrict();
