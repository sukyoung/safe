obj = { x:3, y:5,
        get f() { return 3; },
        set f(z) { } }
with(obj) {
  f()
}
