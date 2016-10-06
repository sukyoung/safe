  var x = 1;
  var y = x++;
  if (y !== 1)
  {
    $ERROR('#1: var x = 1; var y = x++; y === 1. Actual: ' + (y));
  }
  else
  {
    {
      var __result1 = x !== 1 + 1;
      var __expect1 = false;
    }
  }
  this.x = 1;
  var y = this.x++;
  if (y !== 1)
  {
    $ERROR('#2: this.x = 1; var y = this.x++; y === 1. Actual: ' + (y));
  }
  else
  {
    {
      var __result2 = this.x !== 1 + 1;
      var __expect2 = false;
    }
  }
  var object = new Object();
  object.prop = 1;
  var y = object.prop++;
  if (y !== 1)
  {
    $ERROR('#3: var object = new Object(); object.prop = 1; var y = object.prop++; y === 1. Actual: ' + (y));
  }
  else
  {
    {
      var __result3 = this.x !== 1 + 1;
      var __expect3 = false;
    }
  }
  