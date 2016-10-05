  function testcase() 
  {
    try
{      Object.prototype[0] = true;
      Object.prototype[1] = false;
      Object.prototype[2] = "true";
      return 0 === Array.prototype.indexOf.call({
        length : 3
      }, true) && 1 === Array.prototype.indexOf.call({
        length : 3
      }, false) && 2 === Array.prototype.indexOf.call({
        length : 3
      }, "true");}
    finally
{      delete Object.prototype[0];
      delete Object.prototype[1];
      delete Object.prototype[2];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  