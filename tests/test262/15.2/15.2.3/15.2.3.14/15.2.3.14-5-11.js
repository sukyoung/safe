  function testcase() 
  {
    var obj = [1, 2, 3, 4, 5, ];
    var arr = Object.keys(obj);
    var initValue = 0;
    for(var p in arr)
    {
      if (arr.hasOwnProperty(p))
      {
        if (arr[p] !== initValue.toString())
        {
          return false;
        }
        initValue++;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  