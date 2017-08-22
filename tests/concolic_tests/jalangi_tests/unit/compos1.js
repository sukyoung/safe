/*
 * Copyright 2013 Samsung Information Systems America, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Author: Koushik Sen


if (typeof window === "undefined") {
    require('../../src/js/InputManager2');
    require(process.cwd()+'/inputs');
}


function max(a, b) {
    if (a>b) {
        console.log("1");
        return a;
    } else {
        console.log("2");
        return b;
    }
}

function swap(arr, i, j) {
    var tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
}

var arr = [];


function main() {
    for(var i=0; i<4; i++) {
        arr[i] = J$.readInput(i);
    }
    var i, maxm = arr[0];
    for(i=1; i<4; i++) {
        maxm = max(maxm, arr[i]);
    }
}

main();

console.log("3");


