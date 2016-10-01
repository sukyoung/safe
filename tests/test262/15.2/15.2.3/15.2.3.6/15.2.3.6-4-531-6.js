//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = [];
//     var verifySetFunc = "data";
//     var getFunc = (function () 
//     {
//       return verifySetFunc;
//     });
//     Object.defineProperty(obj, "0", {
//       get : getFunc,
//       enumerable : true,
//       configurable : true
//     });
//     obj[0] = "overrideData";
//     var propertyDefineCorrect = obj.hasOwnProperty("0");
//     var desc = Object.getOwnPropertyDescriptor(obj, "0");
//     return propertyDefineCorrect && typeof desc.set === "undefined" && obj[0] === "data";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
