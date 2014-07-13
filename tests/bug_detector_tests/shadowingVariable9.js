function test() {
  i = 4;
  if (0 < i) {
    var i = "t"
    j = i;
  } else {
    var i = "f"
    j = i;
  }
  return i;
}; test();
