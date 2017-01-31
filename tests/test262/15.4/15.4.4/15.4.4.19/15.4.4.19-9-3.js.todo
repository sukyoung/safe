  function testcase() 
  {
    foo.prototype = new Array(1, 2, 3);
    function foo() 
    {
      
    }
    var f = new foo();
    f.length = 1;
    function cb() 
    {
      
    }
    var a = f.map(cb);
    if (Array.isArray(a) && a.length === 1)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  