  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(String, "fromCharCode");
    if (desc.value === String.fromCharCode && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  