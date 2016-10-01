//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = [];
//     obj.verifySetFunc = "data";
//     var getFunc = (function () 
//     {
//       return obj.verifySetFunc;
//     });
//     var setFunc = (function (value) 
//     {
//       obj.verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "0", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : false
//     });
//     obj[0] = "overrideData";
//     var propertyDefineCorrect = obj.hasOwnProperty("0");
//     var desc = Object.getOwnPropertyDescriptor(obj, "0");
//     return propertyDefineCorrect && desc.set === setFunc && obj.verifySetFunc === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
