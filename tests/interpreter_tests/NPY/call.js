var a = 4;
function test1 (b) {
  var a = 7;
  return this.a + b;
}
function test2 (c) {
  this.a = 3;
  return test1.call(this, c);
}

print(test1.call({a:9}, 5));    // 14
print(test2(5));                // 8
