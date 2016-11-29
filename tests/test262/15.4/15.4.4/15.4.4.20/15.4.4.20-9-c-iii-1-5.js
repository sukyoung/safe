  function testcase() 
  {
    var arr = [0, 1, 2, 3, 4, ];
    var lastToIdx = 0;
    var called = 0;
    function callbackfn(val, idx, obj) 
    {
      called++;
      if (lastToIdx !== idx)
      {
        return false;
      }
      else
      {
        lastToIdx++;
        return true;
      }
    }
    var newArr = arr.filter(callbackfn);
    return newArr.length === 5 && called === 5;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  