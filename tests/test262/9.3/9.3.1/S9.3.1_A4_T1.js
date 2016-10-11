  if (Number("+0") !== Number("0"))
  {
    $ERROR('#1.1: Number("+0") === Number("0")');
  }
  else
  {
    {
      var __result1 = 1 / Number("+0") !== 1 / Number("0");
      var __expect1 = false;
    }
  }
  {
    var __result2 = Number("+Infinity") !== Number("Infinity");
    var __expect2 = false;
  }
  {
    var __result3 = Number("+1234.5678") !== Number("1234.5678");
    var __expect3 = false;
  }
  {
    var __result4 = Number("+1234.5678e90") !== Number("1234.5678e90");
    var __expect4 = false;
  }
  {
    var __result5 = Number("+1234.5678E90") !== Number("1234.5678E90");
    var __expect5 = false;
  }
  {
    var __result6 = Number("+1234.5678e-90") !== Number("1234.5678e-90");
    var __expect6 = false;
  }
  {
    var __result7 = Number("+1234.5678E-90") !== Number("1234.5678E-90");
    var __expect7 = false;
  }
  