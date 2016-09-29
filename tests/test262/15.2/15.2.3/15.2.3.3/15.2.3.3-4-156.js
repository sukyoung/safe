  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Date.prototype, "toTimeString");
    if (desc.value === Date.prototype.toTimeString && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  