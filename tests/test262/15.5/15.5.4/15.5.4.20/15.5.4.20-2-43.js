//  TODO [[DefaultValue]]
// function testcase() 
// {
//   var toStringAccessed = false;
//   var valueOfAccessed = false;
//   var proto = {
//     toString : (function () 
//     {
//       toStringAccessed = true;
//       return "abc";
//     })
//   };
//   var Con = (function () 
//   {

//   });
//   Con.prototype = proto;
//   var child = new Con();
//   child.valueOf = (function () 
//   {
//     valueOfAccessed = true;
//     return "efg";
//   });
//   return (String.prototype.trim.call(child) === "abc") && toStringAccessed && ! valueOfAccessed;
// }
// {
//   var __result1 = testcase();
//   var __expect1 = true;
// }

