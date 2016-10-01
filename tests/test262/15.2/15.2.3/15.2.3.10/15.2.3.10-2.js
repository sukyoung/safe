  function testcase() 
  {
    var o = {
      
    };
    var o2 = undefined;
    o2 = Object.preventExtensions(o);
    if (o2 === o && Object.isExtensible(o2) === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  