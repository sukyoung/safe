  function testcase() 
  {
    var kIndex = [];
    var called = 0;
    function callbackfn(val, idx, obj) 
    {
      called++;
      if (kIndex[idx] === undefined)
      {
        if (idx !== 0 && kIndex[idx - 1] === undefined)
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
    var newArr = [11, 12, 13, 14, ].filter(callbackfn, undefined);
    return newArr.length === 0 && called === 4;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  