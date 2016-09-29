  function testcase() 
  {
    var proto = {
      
    };
    Object.preventExtensions(proto);
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var obj = new ConstructFun();
    Object.preventExtensions(obj);
    return ! Object.isExtensible(obj);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  