  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(Function.prototype, "bind");
    if (desc.value === Function.prototype.bind && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  