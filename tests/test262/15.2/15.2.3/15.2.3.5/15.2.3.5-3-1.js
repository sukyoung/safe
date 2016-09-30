  function testcase() 
  {
    function base() 
    {
      
    }
    var b = new base();
    var d = Object.create(b);
    if (Object.getPrototypeOf(d) === b && b.isPrototypeOf(d) === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  