  function dynaString(s1, s2) 
  {
    return String(s1) + String(s2);
  }
  if (Number(dynaString("-", "0")) !== - Number("0"))
  {
    $ERROR('#1: Number("-"+"0") === -Number("0")');
  }
  else
  {
    {
      var __result1 = 1 / Number(dynaString("-", "0")) !== - 1 / Number("0");
      var __expect1 = false;
    }
  }
  {
    var __result2 = Number(dynaString("-Infi", "nity")) !== - Number("Infinity");
    var __expect2 = false;
  }
  {
    var __result3 = Number(dynaString("-12345", "67890")) !== - Number("1234567890");
    var __expect3 = false;
  }
  {
    var __result4 = Number(dynaString("-1234.", "5678")) !== - Number("1234.5678");
    var __expect4 = false;
  }
  {
    var __result5 = Number(dynaString("-1234.", "5678e90")) !== - Number("1234.5678e90");
    var __expect5 = false;
  }
  {
    var __result6 = Number(dynaString("-1234.", "5678E90")) !== - Number("1234.5678E90");
    var __expect6 = false;
  }
  {
    var __result7 = Number(dynaString("-1234.", "5678e-90")) !== - Number("1234.5678e-90");
    var __expect7 = false;
  }
  {
    var __result8 = Number(dynaString("-1234.", "5678E-90")) !== - Number("1234.5678E-90");
    var __expect8 = false;
  }
  {
    var __result9 = Number(dynaString("-Infi", "nity")) !== Number.NEGATIVE_INFINITY;
    var __expect9 = false;
  }
  