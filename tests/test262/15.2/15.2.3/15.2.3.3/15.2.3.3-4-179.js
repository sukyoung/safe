  function testcase() 
  {
    var desc = Object.getOwnPropertyDescriptor(@Global, "Infinity");
    if (desc.writable === false && desc.enumerable === false && desc.configurable === false && desc.hasOwnProperty('get') === false && desc.hasOwnProperty('set') === false)
    {
      return true;
    }
    return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
