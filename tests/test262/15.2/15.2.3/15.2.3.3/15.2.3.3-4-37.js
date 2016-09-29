  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Function.prototype, "call");
    if (desc.value === Function.prototype.call && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  