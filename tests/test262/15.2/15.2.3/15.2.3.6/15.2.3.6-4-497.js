//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var getFunc = (function () 
//     {
//       return 1001;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : undefined,
//       enumerable : true,
//       configurable : true
//     });
//     var propertyDefineCorrect = obj.hasOwnProperty("prop");
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     delete obj.prop;
//     return propertyDefineCorrect && desc.configurable === true && ! obj.hasOwnProperty("prop");
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
