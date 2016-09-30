  function testcase() 
  {
    var o = {
      x : 1,
      y : 2
    };
    var a = Object.keys(o);
    if (Object.isExtensible(a) === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  