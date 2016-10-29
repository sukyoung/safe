  function testcase() 
  {
    var kIndex = [];
    function callbackfn(val, idx, obj) 
    {
      if (typeof kIndex[idx] === "undefined")
      {
        if (idx !== 0 && typeof kIndex[idx - 1] === "undefined")
        {
          return true;
        }
        kIndex[idx] = 1;
        return false;
      }
      else
      {
        return true;
      }
    }
    var testResult = [11, 12, 13, 14, ].map(callbackfn);
    return testResult.length === 4 && testResult[0] === false && testResult[1] === false && testResult[2] === false && testResult[3] === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  