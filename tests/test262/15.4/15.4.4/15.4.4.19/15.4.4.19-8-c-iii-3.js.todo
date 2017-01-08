  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return 11;
    }
    var obj = {
      0 : 11,
      1 : 9,
      length : 2
    };
    var newArr = Array.prototype.map.call(obj, callbackfn);
    try
{      var tempVal = newArr[1];
      newArr[1] += 1;
      return newArr[1] !== tempVal;}
    catch (ex)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  