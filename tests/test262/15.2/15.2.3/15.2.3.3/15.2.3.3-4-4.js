  function testcase() 
  {
    var global = @Global;
    var desc = Object.getOwnPropertyDescriptor(global, "eval");
    if (desc.value === global.eval && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
