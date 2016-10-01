//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function getFunc() 
//     {
//       return 10;
//     }
//     function setFunc(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : getFunc,
//       set : setFunc,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       set : undefined,
//       get : getFunc
//     });
//     var desc = Object.getOwnPropertyDescriptor(obj, "foo");
//     return obj.hasOwnProperty("foo") && typeof (desc.set) === "undefined";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
