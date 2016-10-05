  function testcase() 
  {
    var obj = {
      
    };
    var proto = {
      
    };
    Object.defineProperty(proto, "prop", {
      value : "inheritedValue",
      enumerable : false,
      configurable : true,
      writable : true
    });
    var ConstructFun = (function () 
    {
      
    });
    ConstructFun.prototype = proto;
    var child = new ConstructFun();
    Object.defineProperty(child, "prop1", {
      value : "overridedValue1",
      enumerable : false
    });
    Object.defineProperty(child, "prop2", {
      value : "overridedValue2",
      enumerable : true
    });
    var accessedProp1 = false;
    var accessedProp2 = false;
    for(var p in child)
    {
      if (child.hasOwnProperty(p))
      {
        if (p === "prop1")
        {
          accessedProp1 = true;
        }
        if (p === "prop2")
        {
          accessedProp2 = true;
        }
      }
    }
    return ! accessedProp1 && accessedProp2 && child.prop1 === "overridedValue1" && child.prop2 === "overridedValue2";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  