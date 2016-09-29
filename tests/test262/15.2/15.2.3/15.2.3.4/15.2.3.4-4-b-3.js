  function testcase() 
  {
    var obj = {
      "" : "empty"
    };
    var result = Object.getOwnPropertyNames(obj);
    for(var p in result)
    {
      if (result[p] === "")
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
  