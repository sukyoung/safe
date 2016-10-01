//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function setFunc(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     function getFunc() 
//     {
//       return 10;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : getFunc,
//       set : setFunc,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       get : getFunc,
//       configurable : false
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", getFunc, setFunc, "setVerifyHelpProp", false, 
//     false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
