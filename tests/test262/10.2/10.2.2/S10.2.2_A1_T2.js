  var x = 0;
  function f1() 
  {
    function f2() 
    {
      return x;
    }
    ;
    return f2();
  }
  {
    var __result1 = ! (f1() === 0);
    var __expect1 = false;
  }
  