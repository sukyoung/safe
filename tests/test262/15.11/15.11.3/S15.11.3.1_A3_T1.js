  {
    var __result1 = ! (Error.hasOwnProperty('prototype'));
    var __expect1 = false;
  }
  __obj = Error.prototype;
  Error.prototype = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Error.prototype !== __obj;
    var __expect2 = false;
  }
  try
{    Error.prototype();
    $ERROR('#3: "Error.prototype()" lead to throwing exception');}
  catch (e)
{    ;}

  