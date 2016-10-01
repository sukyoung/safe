function testcase() 
{
  var str = new String("abc");
  var tempArray = [];
  for(var p in str)
  {
    if (str.hasOwnProperty(p))
    {
      tempArray.push(p);
    }
  }
  var returnedArray = Object.keys(str);
  for(var index in returnedArray)
  {
    if (tempArray[index] !== returnedArray[index])
    {
      return false;
    }
  }
  return true;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}
