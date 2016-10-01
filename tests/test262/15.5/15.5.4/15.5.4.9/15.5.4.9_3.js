  var thisValues = ["a", "t", "u", "undefined", "UNDEFINED", "nicht definiert", "xyz", "未定义", ];
  var i;
  for (i = 0;i < thisValues.length;i++)
  {
    var thisValue = thisValues[i];
    {
      var __result1 = thisValue.localeCompare() !== thisValue.localeCompare(undefined);
      var __expect1 = false;
    }
    {
      var __result2 = thisValue.localeCompare(undefined) !== thisValue.localeCompare("undefined");
      var __expect2 = false;
    }
  }
  