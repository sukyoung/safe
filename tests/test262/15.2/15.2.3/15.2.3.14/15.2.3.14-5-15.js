  function testcase() 
  {
    var obj = new String("xyz");
    obj[- 20] = - 20;
    obj[20] = 20;
    Object.defineProperty(obj, "prop", {
      value : 1003,
      enumerable : false,
      configurable : true
    });
    var arr = Object.keys(obj);
    for(var i = 0;i < arr.length;i++)
    {
      if (! obj.hasOwnProperty(arr[i]))
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
  