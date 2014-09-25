(function () {
location.hash="";
location.pathname="/";
location.hostname="";
location.host="";
navigator.userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/6.1.3 Safari/537.75.14";
Math.random = function() { return 32;};
navigator.cookieEnabled = false
document.cookie = "";
// Plug-ins
var __temp = function() {this.length = 0;};
__temp.prototype = PluginArray.prototype;
navigator.plugins = new __temp();
// local storages
var __temp = function() {this.length = 0;};
__temp.prototype = Storage.prototype;
window.localStorage = new __temp();

// constant Date object

Date.prototype.getDate = function () { return 25};
Date.prototype.getDay = function () { return 1};
Date.prototype.getHours = function () { return 17};
Date.prototype.getMilliseconds = function () { return 879};
Date.prototype.getMinutes = function () { return 47};
Date.prototype.getMonth = function () { return 7};
Date.prototype.getSeconds = function () { return 22};
Date.prototype.getTime = function () { return 1408956442879};
Date.prototype.getTimezoneOffset = function () { return -540};
Date.prototype.getFullYear = function () { return 2014};
Date.prototype.getUTCDate = function () { return 25};
Date.prototype.getUTCDay = function () { return 1};
Date.prototype.getUTCFullYear = function () { return 2014};
Date.prototype.getUTCHours = function () { return 8};
Date.prototype.getUTCMilliseconds = function () { return 879};
Date.prototype.getUTCMinutes = function () { return 47};
Date.prototype.getUTCMonth = function () { return 7};
Date.prototype.getUTCSecond = function () { return 22};
Date.prototype.getYear = function () { return 114};
Date.prototype.toDateString = function() { return "Mon Aug 25 2014"};
Date.prototype.toGMTString = function() { return "Mon, 25 Aug 2014 08:47:22 GMT"};
Date.prototype.toISOString = function() { return "2014-08-25T08:47:22.879Z"};
Date.prototype.toJSON = function() { return "2014-08-25T08:47:22.879Z"};
Date.prototype.toLocaleDateString = function() { return "August 25, 2014"};
Date.prototype.toLocaleTimeString = function() { return "5:47:22 PM GMT+09:00"};
Date.prototype.toTimeString = function() { return "17:47:22 GMT+0900 (KST)"};
Date.prototype.toUTCString = function() { return "Mon, 25 Aug 2014 08:47:22 GMT"};
Date.prototype.toString = function () { return "Mon Aug 25 2014 17:47:22 GMT+0900 (KST)"};
Date.prototype.valueOf = function () { return 1408956442879};

var __temp = Date.prototype;
var __temp2 = Date;

Date = function() {
  var __o =  new __temp2(114, 7, 25, 17, 47, 22, 879);
  __o.setTime(1408956442879);
  return __o;
};

Date.now = function() { return 1409104664880;}
Date.UTC = __temp2.UTC;
Date.parse = __temp2.parse;

Date.prototype = __temp;


})();

