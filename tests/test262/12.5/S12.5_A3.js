  try
{    if ((function () 
    {
      throw 1;
    })())
      abracadabra;}
  catch (e)
{    {
      var __result1 = e !== 1;
      var __expect1 = false;
    }}

  try
{    if ((function () 
    {
      throw 1;
    })())
      abracadabra;
    else
      blablachat;}
  catch (e)
{    {
      var __result2 = e !== 1;
      var __expect2 = false;
    }}

  