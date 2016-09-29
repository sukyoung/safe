  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Date, "UTC");
    if (desc.value === Date.UTC && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  