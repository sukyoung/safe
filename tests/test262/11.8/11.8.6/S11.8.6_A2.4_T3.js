  try
{    object instanceof (object = {
      
    }, Object);
    $ERROR('#1.1: object instanceof (object = {}, Object) throw ReferenceError. Actual: ' + (object instanceof (object = {
      
    }, Object)));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  {
    var __result2 = (OBJECT = Object, {
      
    }) instanceof OBJECT !== true;
    var __expect2 = false;
  }
  