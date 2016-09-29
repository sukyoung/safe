  function testcase() 
  {
    var proto = {
      "parent" : "parent"
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    var result = Object.getOwnPropertyNames(child);
    for(var p in result)
    {
      if (result[p] === "parent")
      {
        return false;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  