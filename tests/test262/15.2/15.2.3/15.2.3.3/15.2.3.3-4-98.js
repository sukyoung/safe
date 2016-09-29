  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Math, "asin");
    if (desc.value === Math.asin && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  