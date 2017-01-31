  function testcase() 
  {
    var o = new Object();
    o.srcArr = [1, 2, 3, 4, 5, ];
    function callbackfn(val, idx, obj) 
    {
      delete o.srcArr;
      if (val > 0)
        return true;
      else
        return false;
    }
    var resArr = o.srcArr.filter(callbackfn);
    return resArr.length === 5 && typeof o.srcArr === "undefined";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  