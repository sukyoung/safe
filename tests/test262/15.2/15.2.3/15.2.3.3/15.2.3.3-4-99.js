  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "atan");
    if (desc.value === Math.atan && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  