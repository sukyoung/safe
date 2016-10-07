  try
{    "toString" in true;
    $ERROR('#1: "toString" in true throw TypeError');}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    "MAX_VALUE" in 1;
    $ERROR('#2: "MAX_VALUE" in 1 throw TypeError');}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  try
{    "length" in "string";
    $ERROR('#3: "length" in "string" throw TypeError');}
  catch (e)
{    {
      var __result3 = (e instanceof TypeError) !== true;
      var __expect3 = false;
    }}

  try
{    "toString" in undefined;
    $ERROR('#4: "toString" in undefined throw TypeError');}
  catch (e)
{    {
      var __result4 = (e instanceof TypeError) !== true;
      var __expect4 = false;
    }}

  try
{    "toString" in null;
    $ERROR('#5: "toString" in null throw TypeError');}
  catch (e)
{    {
      var __result5 = (e instanceof TypeError) !== true;
      var __expect5 = false;
    }}

  