  function f1(x) 
  {
    var x;
    return typeof x;
  }
  if (! (f1() === "undefined"))
  {
    $PRINT('#1: f1(1) === "undefined"');
  }
  function f2(x) 
  {
    var x;
    return x;
  }
  if (! (f2() === undefined))
  {
    $PRINT('#1: f2(1) === undefined');
  }
  