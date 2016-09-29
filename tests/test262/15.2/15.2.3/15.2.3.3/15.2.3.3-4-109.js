  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "random");
    if (desc.value === Math.random && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  