  {
    var __result1 = (1, 2) !== 2;
    var __expect1 = false;
  }
  var x = 1;
  {
    var __result2 = (x, 2) !== 2;
    var __expect2 = false;
  }
  var y = 2;
  {
    var __result3 = (1, y) !== 2;
    var __expect3 = false;
  }
  var x = 1;
  var y = 2;
  {
    var __result4 = (x, y) !== 2;
    var __expect4 = false;
  }
  var x = 1;
  {
    var __result5 = (x, x) !== 1;
    var __expect5 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = true;
  objecty.prop = 1.1;
  if ((objectx.prop = false, objecty.prop) !== objecty.prop)
  {
    $ERROR('#6: var objectx = new Object(); var objecty = new Object(); objectx.prop = true; objecty.prop = 1; (objectx.prop = false, objecty.prop) === objecty.prop. Actual: ' + (objectx.prop = false, objecty.prop));
  }
  else
  {
    {
      var __result6 = objectx.prop !== false;
      var __expect6 = false;
    }
  }
  