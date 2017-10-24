  function testcase() 
  {
    var arrObj = [0, 1, 2, ];
    var arrProtoLen;
    try
{      arrProtoLen = Array.prototype.length;
      Array.prototype.length = 0;
      Object.defineProperty(arrObj, "2", {
        configurable : false
      });
      Object.defineProperty(arrObj, "length", {
        value : 1
      });
      return false;}
    catch (e)
{      return e instanceof TypeError && arrObj.length === 3 && Array.prototype.length === 0;}

    finally
{      Array.prototype.length = arrProtoLen;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  