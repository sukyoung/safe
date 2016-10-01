  function testcase() 
  {
    var proto = {
      
    };
    var preCheck = Object.isExtensible(proto);
    Object.preventExtensions(proto);
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var child = new ConstructFun();
    child.prop = 10;
    return preCheck && child.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  