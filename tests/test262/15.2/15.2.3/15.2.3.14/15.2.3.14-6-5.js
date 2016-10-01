function testcase() 
{
  var obj = new Date();
  obj.prop1 = 100;
  obj.prop2 = "prop2";
  var tempArray = [];
  for(var p in obj)
  {
    if (obj.hasOwnProperty(p))
    {
      tempArray.push(p);
    }
  }
  var returnedArray = Object.keys(obj);
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
