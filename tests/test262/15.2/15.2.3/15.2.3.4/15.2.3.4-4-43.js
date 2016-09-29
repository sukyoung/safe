  function testcase() 
  {
    var str = new String("abc");
    Object.defineProperty(str, "ownProperty", {
      get : (function () 
      {
        return "ownString";
      }),
      configurable : true
    });
    var result = Object.getOwnPropertyNames(str);
    for(var p in result)
    {
      if (result[p] === "ownProperty")
      {
        return true;
      }
    }
    return false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  