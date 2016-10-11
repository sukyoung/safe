  try
{    var __obj = {
      toString : (function () 
      {
        return "1";
      }),
      valueOf : (function () 
      {
        return new Object();
      })
    };
    {
      var __result1 = Number(__obj) !== 1;
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR('#1.2: var __obj = {toNumber: function() {return "1"}, valueOf: function() {return new Object();}}; Number(__obj) === 1. Actual: ' + (e));}

  