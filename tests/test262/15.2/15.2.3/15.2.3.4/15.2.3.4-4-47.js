  function testcase() 
  {
    var arr = [0, 1, 2, ];
    arr.ownProperty = "ownArray";
    var result = Object.getOwnPropertyNames(arr);
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
  