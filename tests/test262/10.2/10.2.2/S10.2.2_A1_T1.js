  var x = 0;
  function f1() 
  {
    var x = 1;
    function f2() 
    {
      return x;
    }
    ;
    return f2();
  }
  {
    var __result1 = ! (f1() === 1);
    var __expect1 = false;
  }
  