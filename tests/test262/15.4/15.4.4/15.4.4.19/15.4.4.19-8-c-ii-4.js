  function testcase() 
  {
    var arr = [0, 1, 2, 3, 4, 5, ];
    var lastIdx = 0;
    var called = 0;
    var result = true;
    function callbackfn(val, idx, o) 
    {
      called++;
      if (lastIdx !== idx)
      {
        result = false;
      }
      else
      {
        lastIdx++;
      }
    }
    arr.map(callbackfn);
    return result && arr.length === called;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  