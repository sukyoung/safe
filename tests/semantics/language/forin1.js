function Foo () {}

Foo.prototype = { 1: 'a', 2: 'b', 3: 'c' }

var foo = new Foo();

var __result = "";
for (var prop in foo) {
  __result += foo[prop];
}

var __expect = "abc"
