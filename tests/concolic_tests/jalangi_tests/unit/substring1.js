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
    require('../../src/js/InputManager');
    require(process.cwd() + '/inputs');
}


var s = J$.readInput("");
var c;
var ret = J$.readInput(0);
c = s.substring(1, 2);
if (c !== '') {
    var y = String.fromCharCode(ret);
    J$.addAxiom(c === y);
    console.log("1");
} else {
    console.log("2");
}
if (ret === 73) {
    console.log("3");
}
console.log("4");

