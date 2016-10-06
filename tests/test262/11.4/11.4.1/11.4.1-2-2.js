  function testcase() 
  {
    var bIsFooCalled = false;
    var foo = (function () 
    {
      bIsFooCalled = true;
    });
    var d = delete foo();
    if (d === true && bIsFooCalled === true)
      return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  