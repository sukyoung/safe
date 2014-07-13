"use strict";

delete NaN;
delete this.NaN;
delete this[NaN];

function notStrict() {
    delete NaN;
    delete this.NaN;
    delete this[NaN];
}

notStrict();
