  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return (idx === 1) && (val === 13);
    }
    try
{      Array.prototype[1] = 13;
      var newArr = [, , , ].filter(callbackfn);
      return newArr.length === 1 && newArr[0] === 13;}
    finally
{      delete Array.prototype[1];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  