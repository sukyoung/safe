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

var x;

function f1(x) {
    J$.addAxiom(x===1);
}

function f2(x) {
    if (x>10){
        console.log("9");
    } else {
        console.log("1");
    }
}


x = J$.readInput(-2);
f1(x);
f2(x);
console.log("2");



