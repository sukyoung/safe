  function testcase() 
  {
    var global = @Global;
    var desc = Object.getOwnPropertyDescriptor(global, "parseInt");
    if (desc.value === global.parseInt && desc.writable === true && desc.enumerable === false && desc.configurable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
