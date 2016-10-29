  try
{    var __obj = {
      toString : (function () 
      {
        return new Object();
      })
    };
    String(__obj);
    $ERROR('#1.1: var __obj = {toString: function() {return new Object();}}; String(__obj) throw TypeError. Actual: ' + (String(__obj)));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  