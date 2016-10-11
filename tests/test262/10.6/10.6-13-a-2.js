  function testcase() 
  {
    var called = false;
    function test1(flag) 
    {
      if (flag !== true)
      {
        test2();
      }
      else
      {
        called = true;
      }
    }
    function test2() 
    {
      if (arguments.callee.caller === undefined)
      {
        called = true;
      }
      else
      {
        arguments.callee.caller(true);
      }
    }
    test1();
    return called;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  