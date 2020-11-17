function g(x) {
  return function () { return x; }
}
for (i = 0; i < 100; i++) {
  g(42);
}
