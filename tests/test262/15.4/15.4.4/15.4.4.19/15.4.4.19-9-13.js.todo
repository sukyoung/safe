  function testcase() 
  {
    var called = 0;
    function callbackfn(val, idx, obj) 
    {
      called++;
      return val > 2;
    }
    var arr = [1, 2, 3, 4, ];
    arr.map(callbackfn);
    return 1 === arr[0] && 2 === arr[1] && 3 === arr[2] && 4 === arr[3] && 4 === called;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  