  function testcase() 
  {
    var result = true;
    var kIndex = [];
    function callbackfn(val, idx, obj) 
    {
      if (typeof kIndex[idx] === "undefined")
      {
        if (idx !== 0 && typeof kIndex[idx - 1] === "undefined")
        {
          result = false;
        }
        kIndex[idx] = 1;
      }
      else
      {
        result = false;
      }
    }
    [11, 12, 13, 14, ].forEach(callbackfn, undefined);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  