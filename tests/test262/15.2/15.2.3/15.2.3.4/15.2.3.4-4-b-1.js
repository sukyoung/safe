  function testcase() 
  {
    var obj = new Object();
    obj.x = 1;
    obj.y = 2;
    var result = Object.getOwnPropertyNames(obj);
    var desc = Object.getOwnPropertyDescriptor(result, "0");
    if (desc.enumerable === true && desc.configurable === true && desc.writable === true)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  