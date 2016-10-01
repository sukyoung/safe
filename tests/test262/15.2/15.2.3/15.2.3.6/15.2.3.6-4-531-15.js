//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = [];
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     var getFunc = (function () 
//     {
//       return verifySetFunc;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     obj.prop = "overrideData";
//     var propertyDefineCorrect = obj.hasOwnProperty("prop");
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     return propertyDefineCorrect && desc.set === setFunc && obj.prop === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
