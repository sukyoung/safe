function test() {
  var value = 10;
  value--;
  var value = 3;
  value--;
  var value = 15;
  value--;
  var max = 8;
  if (max < value) value--;
  var max = 6;
  return max < value;
}; test();
