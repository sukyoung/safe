  try
{    var __obj = {
      toString : (function () 
      {
        return new Object();
      }),
      valueOf : (function () 
      {
        return 1;
      })
    };
    {
      var __result1 = String(__obj) !== "1";
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR('#1.2: var __obj = {toString: function() {return new Object();}, valueOf: function() {return 1;}}; String(__obj) === "1". Actual: ' + (e));}

  