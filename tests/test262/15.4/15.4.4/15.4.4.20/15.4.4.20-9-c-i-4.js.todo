  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return (idx === 0) && (val === 12);
    }
    try
{      Array.prototype[0] = 11;
      var newArr = [12, ].filter(callbackfn);
      return newArr.length === 1 && newArr[0] === 12;}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  