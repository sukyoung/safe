  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "cos");
    if (desc.value === Math.cos && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  