function testcase() 
{
  var denseArray = [1, 2, 3, ];
  var tempArray = [];
  for(var p in denseArray)
  {
    if (denseArray.hasOwnProperty(p))
    {
      tempArray.push(p);
    }
  }
  var returnedArray = Object.keys(denseArray);
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
