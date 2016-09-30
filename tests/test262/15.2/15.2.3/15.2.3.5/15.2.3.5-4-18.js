  function testcase() 
  {
    var proto = {
      
    };
    proto.prop = {
      
    };
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var child = new ConstructFun();
    var newObj = Object.create({
      
    }, child);
    return ! newObj.hasOwnProperty("prop");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  