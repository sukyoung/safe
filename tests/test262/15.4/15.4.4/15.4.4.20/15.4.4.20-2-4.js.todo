  function testcase() 
  {
    var arrProtoLen;
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    try
{      arrProtoLen = Array.prototype.length;
      Array.prototype.length = 0;
      var newArr = [12, 11, ].filter(callbackfn);
      return newArr.length === 2;}
    finally
{      Array.prototype.length = arrProtoLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  