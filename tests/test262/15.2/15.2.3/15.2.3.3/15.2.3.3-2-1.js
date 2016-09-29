  function testcase() 
  {
    var o = {
      
    };
    var desc = Object.getOwnPropertyDescriptor(o, undefined);
    if (desc === undefined)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  