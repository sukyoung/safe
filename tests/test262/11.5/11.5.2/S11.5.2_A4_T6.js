var __result1 = false;
  if (1 / Number.NEGATIVE_INFINITY !== - 0)
  {
      var __result1 = true;
  }
  else
  {
    {
      var __result1 = 1 / (1 / Number.NEGATIVE_INFINITY) !== Number.NEGATIVE_INFINITY;
      var __expect1 = false;
    }
  }

var __result2 = false;
  if (- 1 / Number.NEGATIVE_INFINITY !== + 0)
  {
      var __result2 = true;
  }
  else
  {
    {
      var __result2 = 1 / (- 1 / Number.NEGATIVE_INFINITY) !== Number.POSITIVE_INFINITY;
      var __expect2 = false;
    }
  }

var __result3 = false;
  if (1 / Number.POSITIVE_INFINITY !== + 0)
  {
      var __result3 = true;
  }
  else
  {
    {
      var __result3 = 1 / (1 / Number.POSITIVE_INFINITY) !== Number.POSITIVE_INFINITY;
      var __expect3 = false;
    }
  }

      var __result4 = false;
  if (- 1 / Number.POSITIVE_INFINITY !== - 0)
  {
      var __result4 = true;
  }
  else
  {
    {
      var __result4 = 1 / (- 1 / Number.POSITIVE_INFINITY) !== Number.NEGATIVE_INFINITY;
      var __expect4 = false;
    }
  }
  
