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
//       obj.verifyGetHelpMethod = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : getFunc,
//       set : setFunc,
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         get : getFunc
//       });
//       return accessorPropertyAttributesAreCorrect(obj, "foo", getFunc, setFunc, "verifyGetHelpMethod", 
//       false, 
//       false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
