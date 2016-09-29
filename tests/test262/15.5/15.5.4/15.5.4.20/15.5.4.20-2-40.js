//  TODO [[DefaultValue]]
// function testcase() 
// {
//   var toStringAccessed = false;
//   var valueOfAccessed = false;
//   var obj = {
//     toString : (function () 
//     {
//       toStringAccessed = true;
//       return {

//       };
//     }),
//     valueOf : (function () 
//     {
//       valueOfAccessed = true;
//       return "abc";
//     })
//   };
//   return (String.prototype.trim.call(obj) === "abc") && valueOfAccessed && toStringAccessed;
// }
// {
//   var __result1 = testcase();
//   var __expect1 = true;
// }

