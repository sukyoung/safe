  function testcase() 
  {
    var obj = {
      
    };
    var preCheck = Object.isExtensible(obj);
    Object.preventExtensions(obj);
    obj[0] = 12;
    return preCheck && ! obj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  