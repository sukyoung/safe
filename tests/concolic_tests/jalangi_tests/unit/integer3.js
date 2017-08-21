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

function f(a, b) {
	b = 3*b + 1*a - 0*a;
	if (b === 3) {
			_<>_print("1");
	} else if (b === 7) {
			_<>_print("2");
	} else {
			_<>_print("3");
	}
	_<>_print("4");
}

f(1,1);


