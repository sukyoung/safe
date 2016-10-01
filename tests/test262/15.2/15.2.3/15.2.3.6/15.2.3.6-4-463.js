//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : undefined,
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     Object.defineProperty(obj, "prop", {
//       set : undefined
//     });
//     var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//     return desc1.set === setFunc && typeof desc2.set === "undefined";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
