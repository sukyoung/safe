  function testcase() 
  {
    var obj = {
      prop : 12
    };
    var preCheck = Object.isExtensible(obj);
    Object.preventExtensions(obj);
    delete obj.prop;
    return preCheck && ! obj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  