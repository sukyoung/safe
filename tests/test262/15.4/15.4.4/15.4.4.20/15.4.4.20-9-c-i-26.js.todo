  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (idx === 0)
      {
        return val === 11;
      }
      else
        if (idx === 1)
        {
          return val === 9;
        }
        else
        {
          return false;
        }
    }
    var func = (function (a, b) 
    {
      return Array.prototype.filter.call(arguments, callbackfn);
    });
    var newArr = func(11, 9);
    return newArr.length === 2 && newArr[0] === 11 && newArr[1] === 9;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  