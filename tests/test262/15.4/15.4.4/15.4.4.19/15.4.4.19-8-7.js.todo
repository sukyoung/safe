  function testcase() 
  {
    var obj = {
      
    };
    obj.srcArr = [1, 2, 3, 4, 5, ];
    function callbackfn(val, idx, obj) 
    {
      delete obj.srcArr;
      if (val > 0)
        return 1;
      else
        return 0;
    }
    var resArr = obj.srcArr.map(callbackfn);
    return resArr.toString() === "1,1,1,1,1" && ! obj.hasOwnProperty("arr");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  