  function testcase() 
  {
    var proto = {
      
    };
    proto.prop = {
      value : "abc"
    };
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var child = new ConstructFun();
    child.prop = {
      value : "bbq"
    };
    var newObj = Object.create({
      
    }, child);
    return newObj.hasOwnProperty("prop") && newObj.prop === "bbq";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  