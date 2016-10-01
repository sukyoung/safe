  var err1 = new Error("Error");
  try
{    var toStr = err1.toString();}
  catch (e)
{    $ERROR('#1: var err1=new Error("Error"); var toStr=err1.toString(); lead to throwing exception. Exception is ' + e);}

  {
    var __result1 = toStr === undefined;
    var __expect1 = false;
  }
  