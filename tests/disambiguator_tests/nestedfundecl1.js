y = 1;
obj = {y:10};
with (obj) {
  function f() {
    return y;
  }
}

alert(f());
f();
y;
