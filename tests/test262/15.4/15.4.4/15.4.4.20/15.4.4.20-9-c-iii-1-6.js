  function testcase() 
  {
    var toIndex = [];
    var called = 0;
    function callbackfn(val, idx, obj) 
    {
      called++;
      if (toIndex[idx] === undefined)
      {
        if (idx !== 0 && toIndex[idx - 1] === undefined)
        {
          return false;
        }
        toIndex[idx] = 1;
        return true;
      }
      else
      {
        return false;
      }
    }
    var newArr = [11, 12, 13, 14, ].filter(callbackfn, undefined);
    return newArr.length === 4 && called === 4;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  