//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     var getFunc = (function () 
//     {
//       return verifySetFunc;
//     });
//     Object.defineProperty(obj, "0", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     obj[0] = "overrideData";
//     var propertyDefineCorrect = obj.hasOwnProperty("0");
//     var desc = Object.getOwnPropertyDescriptor(obj, "0");
//     return propertyDefineCorrect && desc.set === setFunc && obj[0] === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
