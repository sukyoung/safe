  function testcase() 
  {
    var arr = [0, 1, 2, 3, 4, 5, ];
    var lastIdx = 0;
    var called = 0;
    function callbackfn(val, idx, o) 
    {
      called++;
      if (lastIdx !== idx)
      {
        return false;
      }
      else
      {
        lastIdx++;
        return true;
      }
    }
    var newArr = arr.filter(callbackfn);
    return newArr.length === called;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  