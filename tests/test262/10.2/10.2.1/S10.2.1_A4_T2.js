  function f1() 
  {
    var x;
    return x;
    function x() 
    {
      return 7;
    }
  }
  if (! (f1().constructor.prototype === Function.prototype))
  {
    $PRINT('#1: f1() returns function');
  }
  function f2() 
  {
    var x;
    return typeof x;
    function x() 
    {
      return 7;
    }
  }
  if (! (f2() === "function"))
  {
    $PRINT('#2: f2() === "function"');
  }
  