  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return true;
    }
    var obj = {
      0 : 11,
      length : 2
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    var prop;
    var enumerable = false;
    for (prop in newArr)
    {
      if (newArr.hasOwnProperty(prop))
      {
        if (prop === "0")
        {
          enumerable = true;
        }
      }
    }
    return enumerable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  