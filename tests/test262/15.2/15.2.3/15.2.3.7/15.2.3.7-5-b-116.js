  function testcase() 
  {
    var obj = {
      
    };
    var proto = {
      value : "inheritedDataProperty"
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var descObj = new Con();
    descObj.value = "ownDataProperty";
    Object.defineProperties(obj, {
      property : descObj
    });
    return obj.property === "ownDataProperty";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  