  var x = 1;
  if (-- x !== 1 - 1)
  {
    $ERROR('#1: var x = 1; --x === 1 - 1. Actual: ' + (-- x));
  }
  else
  {
    {
      var __result1 = x !== 1 - 1;
      var __expect1 = false;
    }
  }
  this.x = 1;
  if (-- this.x !== 1 - 1)
  {
    $ERROR('#2: this.x = 1; --this.x === 1 - 1. Actual: ' + (-- this.x));
  }
  else
  {
    {
      var __result2 = this.x !== 1 - 1;
      var __expect2 = false;
    }
  }
  var object = new Object();
  object.prop = 1;
  if (-- object.prop !== 1 - 1)
  {
    $ERROR('#3: var object = new Object(); object.prop = 1; --object.prop === 1 - 1. Actual: ' + (-- object.prop));
  }
  else
  {
    {
      var __result3 = this.x !== 1 - 1;
      var __expect3 = false;
    }
  }
  