  try
{    true instanceof true;
    $ERROR('#1: true instanceof true throw TypeError');}
  catch (e)
{    {
      var __result1 = e instanceof TypeError !== true;
      var __expect1 = false;
    }}

  try
{    1 instanceof 1;
    $ERROR('#2: 1 instanceof 1 throw TypeError');}
  catch (e)
{    {
      var __result2 = e instanceof TypeError !== true;
      var __expect2 = false;
    }}

  try
{    "string" instanceof "string";
    $ERROR('#3: "string" instanceof "string" throw TypeError');}
  catch (e)
{    {
      var __result3 = e instanceof TypeError !== true;
      var __expect3 = false;
    }}

  try
{    undefined instanceof undefined;
    $ERROR('#4: undefined instanceof undefined throw TypeError');}
  catch (e)
{    {
      var __result4 = e instanceof TypeError !== true;
      var __expect4 = false;
    }}

  try
{    null instanceof null;
    $ERROR('#5: null instanceof null throw TypeError');}
  catch (e)
{    {
      var __result5 = e instanceof TypeError !== true;
      var __expect5 = false;
    }}

  