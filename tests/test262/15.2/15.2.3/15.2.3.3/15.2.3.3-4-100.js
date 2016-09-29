  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "atan2");
    if (desc.value === Math.atan2 && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  