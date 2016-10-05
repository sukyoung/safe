  try
{    throw "catchme";
    throw "dontcatchme";
    $ERROR('#1.1: throw "catchme" lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e === "dontcatchme";
      var __expect1 = false;
    }
    {
      var __result2 = e !== "catchme";
      var __expect2 = false;
    }}

  function SwitchTest1(value) 
  {
    var result = 0;
    try
{      switch (value){
        case 1:
          result += 4;
          throw result;
          break;

        case 4:
          result += 64;
          throw "ex";

        
      }
      return result;}
    catch (e)
{      if ((value === 1) && (e !== 4))
        $ERROR('#2.1: Exception === 4. Actual: ' + e);
      if ((value === 4) && (e !== "ex"))
        $ERROR('#2.2: Exception === "ex". Actual: ' + e);}

    finally
{      return result;}

  }
  if (SwitchTest1(1) !== 4)
    $ERROR('#2.3: "finally" block must be evaluated');
  if (SwitchTest1(4) !== 64)
    $ERROR('#2.4: "finally" block must be evaluated');
  