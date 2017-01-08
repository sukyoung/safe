  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      if (idx === 1)
      {
        return val === 13;
      }
      return false;
    }
    try
{      Array.prototype[1] = 13;
      var newArr = [, , , ].map(callbackfn);
      return newArr[1] === true;}
    finally
{      delete Array.prototype[1];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  