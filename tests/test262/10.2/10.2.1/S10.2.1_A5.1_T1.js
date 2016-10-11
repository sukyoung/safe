  function f1() 
  {
    var x;
    return typeof x;
  }
  if (! (f1() === "undefined"))
  {
    $PRINT('#1: f1() === "undefined"');
  }
  function f2() 
  {
    var x;
    return x;
  }
  if (! (f2() === undefined))
  {
    $PRINT('#1: f2() === undefined');
  }
  