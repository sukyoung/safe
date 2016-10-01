//   TODO Double.toString()
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     try
// {      __Global.set = (function (value) 
//       {
//         data = value;
//       });
//       Object.defineProperty(obj, "property", __Global);
//       obj.property = "overrideData";
//       return obj.hasOwnProperty("property") && data === "overrideData";}
//     finally
// {      delete __Global.set;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
