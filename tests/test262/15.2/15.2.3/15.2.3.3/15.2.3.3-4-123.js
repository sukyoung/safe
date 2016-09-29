  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Date.prototype, "getDay");
    if (desc.value === Date.prototype.getDay && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  