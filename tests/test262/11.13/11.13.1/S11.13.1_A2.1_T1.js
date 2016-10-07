  x = 1;
  {
    var __result1 = x !== 1;
    var __expect1 = false;
  }
  var x = 1;
  {
    var __result2 = x !== 1;
    var __expect2 = false;
  }
  y = 1;
  x = y;
  {
    var __result3 = x !== 1;
    var __expect3 = false;
  }
  var y = 1;
  var x = y;
  {
    var __result4 = x !== 1;
    var __expect4 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objecty.prop = 1.1;
  objectx.prop = objecty.prop;
  if (objectx.prop !== objecty.prop)
  {
    $ERROR('#5: var objectx = new Object(); var objecty = new Object(); objecty.prop = 1; objectx.prop = objecty.prop; objectx.prop === objecty.prop. Actual: ' + (objectx.prop));
  }
  else
  {
    {
      var __result5 = objectx === objecty;
      var __expect5 = false;
    }
  }
  