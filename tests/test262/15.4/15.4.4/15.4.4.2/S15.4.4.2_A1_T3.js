  var x = new Array("", "", "");
  if (x.toString() !== x.join())
  {
    $ERROR('#0.1: var x = new Array("","",""); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result1 = x.toString() !== ",,";
      var __expect1 = false;
    }
  }
  var x = new Array("\\", "\\", "\\");
  if (x.toString() !== x.join())
  {
    $ERROR('#1.1: var x = new Array("\\","\\","\\"); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result2 = x.toString() !== "\\,\\,\\";
      var __expect2 = false;
    }
  }
  var x = new Array("&", "&", "&");
  if (x.toString() !== x.join())
  {
    $ERROR('#2.1: var x = new Array("&", "&", "&"); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result3 = x.toString() !== "&,&,&";
      var __expect3 = false;
    }
  }
  var x = new Array(true, true, true);
  if (x.toString() !== x.join())
  {
    $ERROR('#3.1: var x = new Array(true,true,true); x.toString(true,true,true) === x.join(). Actual: ' + (x.toString(true, true, true)));
  }
  else
  {
    {
      var __result4 = x.toString() !== "true,true,true";
      var __expect4 = false;
    }
  }
  var x = new Array(null, null, null);
  if (x.toString() !== x.join())
  {
    $ERROR('#4.1: var x = new Array(null,null,null); x.toString(null,null,null) === x.join(). Actual: ' + (x.toString(null, null, null)));
  }
  else
  {
    {
      var __result5 = x.toString() !== ",,";
      var __expect5 = false;
    }
  }
  var x = new Array(undefined, undefined, undefined);
  if (x.toString() !== x.join())
  {
    $ERROR('#5.1: var x = new Array(undefined,undefined,undefined); x.toString(undefined,undefined,undefined) === x.join(). Actual: ' + (x.toString(undefined, undefined, undefined)));
  }
  else
  {
    {
      var __result6 = x.toString() !== ",,";
      var __expect6 = false;
    }
  }
  var x = new Array(Infinity, Infinity, Infinity);
  if (x.toString() !== x.join())
  {
    $ERROR('#6.1: var x = new Array(Infinity,Infinity,Infinity); x.toString(Infinity,Infinity,Infinity) === x.join(). Actual: ' + (x.toString(Infinity, Infinity, Infinity)));
  }
  else
  {
    {
      var __result7 = x.toString() !== "Infinity,Infinity,Infinity";
      var __expect7 = false;
    }
  }
  var x = new Array(NaN, NaN, NaN);
  if (x.toString() !== x.join())
  {
    $ERROR('#7.1: var x = new Array(NaN,NaN,NaN); x.toString(NaN,NaN,NaN) === x.join(). Actual: ' + (x.toString(NaN, NaN, NaN)));
  }
  else
  {
    {
      var __result8 = x.toString() !== "NaN,NaN,NaN";
      var __expect8 = false;
    }
  }
  