  this.count = 0;
  var testScreen = {
    touch : (function () 
    {
      count++;
    })
  };
  testScreen.touch();
  {
    var __result1 = count !== 1;
    var __expect1 = false;
  }
  testScreen['touch']();
  {
    var __result2 = count !== 2;
    var __expect2 = false;
  }
  