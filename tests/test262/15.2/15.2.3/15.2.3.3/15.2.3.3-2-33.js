// TODO string escape (rewritten)
function testcase() 
{
  var obj = {
    "ABd" : 1
//    "AB\n\\cd" : 1
  };
//    var desc = Object.getOwnPropertyDescriptor(obj, "AB\n\\cd");
  var desc = Object.getOwnPropertyDescriptor(obj, "ABd");
  return desc.value === 1;
}
var __result1 = testcase();
var __expect1 = true;
