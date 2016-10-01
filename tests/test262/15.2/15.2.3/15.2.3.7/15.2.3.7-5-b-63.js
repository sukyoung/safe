  function testcase() 
  {
    var obj = {
      
    };
    var proto = {
      configurable : true
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var descObj = new Con();
    Object.defineProperty(descObj, "configurable", {
      value : false
    });
    Object.defineProperties(obj, {
      prop : descObj
    });
    var result1 = obj.hasOwnProperty("prop");
    delete obj.prop;
    var result2 = obj.hasOwnProperty("prop");
    return result1 === true && result2 === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  