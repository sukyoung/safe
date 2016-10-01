//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     var obj1 = {
//       length : 10
//     };
//     Object.defineProperty(arrObj, 0, {
//       value : obj1,
//       writable : false,
//       configurable : false
//     });
//     var obj2 = {
//       length : 20
//     };
//     try
// {      Object.defineProperty(arrObj, "0", {
//         value : obj2
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "0", obj1, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
