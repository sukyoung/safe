  function testcase() 
  {
    var proto = {
      value : "inheritedDataProperty"
    };
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var descObj = new ConstructFun();
    descObj.value = "ownDataProperty";
    var newObj = Object.create({
      
    }, {
      prop : descObj
    });
    return newObj.prop === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  