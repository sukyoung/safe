  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "caller");
    if (desc === undefined)
      return true;
    else
      return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  