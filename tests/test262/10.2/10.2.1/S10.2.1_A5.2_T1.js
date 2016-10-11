  function f1(x) 
  {
    var x;
    return typeof x;
  }
  if (! (f1(1) === "number"))
  {
    $PRINT('#1: f1(1) === "number"');
  }
  function f2(x) 
  {
    var x;
    return x;
  }
  if (! (f2(1) === 1))
  {
    $PRINT('#1: f2(1) === 1');
  }
  