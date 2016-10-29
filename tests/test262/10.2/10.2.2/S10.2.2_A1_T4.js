  var x = 0;
  function f1() 
  {
    function f2() 
    {
      return x;
    }
    ;
    var x = 1;
    return f2();
  }
  {
    var __result1 = ! (f1() === 1);
    var __expect1 = false;
  }
  