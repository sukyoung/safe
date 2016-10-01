  function testcase() 
  {
    var o = {
      
    };
    var desc = {
      value : 1
    };
    Object.defineProperty(o, "foo", desc);
    var propDesc = Object.getOwnPropertyDescriptor(o, "foo");
    if (propDesc.value === 1 && propDesc.writable === false && propDesc.enumerable === false && propDesc.configurable === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  