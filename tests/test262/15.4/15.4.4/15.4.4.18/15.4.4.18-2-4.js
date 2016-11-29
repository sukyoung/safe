  function testcase() 
  {
    var result = false;
    var arrProtoLen;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    try
{      arrProtoLen = Array.prototype.length;
      Array.prototype.length = 0;
      [12, 11, ].forEach(callbackfn);
      return result;}
    finally
{      Array.prototype.length = arrProtoLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  