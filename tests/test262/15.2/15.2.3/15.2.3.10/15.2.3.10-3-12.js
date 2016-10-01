  function testcase() 
  {
    var obj = {
      
    };
    var preCheck = Object.isExtensible(obj);
    Object.preventExtensions(obj);
    obj.exName = 2;
    return preCheck && ! Object.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  