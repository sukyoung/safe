// built-in function models
(function() {
var _xx_ = String.prototype.match;
String.prototype.match = function(exp) {
  var reg = RegExp(exp);
  return _xx_.call(this, reg);
};
var _xxxx_ = String.prototype.replace;
String.prototype.replace = function (searchValue, replaceValue) {
  if(typeof replaceValue == "function") {
    var matchedResult = this.match(searchValue);
    if(matchedResult == null)
      return this;
    else {
      if(!searchValue.global) {
          var args = [];
          for(var i = 0; i < matchedResult.length; i++) {
            args[i] = matchedResult[i];   
          }
          args[i] = matchedResult.index;
          args[i+1] = this;
          var returnval = replaceValue.apply(this, args);
          return _xxxx_.call(this, matchedResult[0], returnval);
      }
      else {
        // String Top
        return document.cookie;
        /*
        var result = "";
        var target = this;
        var lastIndex = 0;
        for(var i = 0; i< matchedResult.length; i++){
          var args = [];
          var r1 = target.match(matchedResult[i]);
          if(r1!=null) {
            args[0] = r1[0];
            args[1] = lastIndex + r1.index;
            args[2] = this;
            lastIndex += r1.index + r1[0].length ;
            var rr = target.substr(0, lastIndex);
            target = target.substr(lastIndex);
            var returnval = replaceValue.apply(this, args);   
            var final_result = _xxxx_.call(rr, matchedResult[i], returnval);
            result = result.concat(final_result);
          }
        }
        if(result === "")
          return this;
        else 
          return result;
        */
      }
    }
  }
  else {
    return _xxxx_.call(this, searchValue, replaceValue);
  }
};

Array.prototype.forEach = function(callbackfn, thisArg) {
  var O = this;
  var len = O.length;
  var T;
  if(thisArg)
    T = thisArg
  var k = 0;
  while(k<len) {
    var kPresent = (k in O);
    if(kPresent) {
      var kValue = O[k];
      callbackfn.call(T, kValue, k, O);
    }
    k = k+1;
  }
};

Array.prototype.map = function(callbackfn, thisArg) {
  var O = this;
  var len = O.length;
  var T;
  if(thisArg)
    T = thisArg
  var A = new Array(len);
  var k = 0;
  while(k<len) {
    var kPresent = (k in O);
    if(kPresent) {
      var kValue = O[k];
      var mappedValue = callbackfn.call(T, kValue, k, O);
      A[k] = mappedValue;
    }
    k = k+1;
  }
  return A;
};
  
  Array.prototype.sort = bubbleSort;
  // modeled using bubble sort (http://en.wikipedia.org/wiki/Bubble_sort)
  function bubbleSort(compare) {

    var a = this;
    var n = a.length;

    if (!compare) {
      compare = function(left, right) {
        if (left < right)
          return -1;
        if (left == right)
          return 0;
        else
          return 1;
      };
    }
    
    do {
      var newn = 0;
      for(var i=1; i < n; i++){
        var result = compare(a[i-1], a[i]);
        // a[i-1] > a[i]
        if(result==1) {
          var temp = a[i-1];
          a[i-1] = a[i];
          a[i] = temp;
          newn = i;
        }
      }
      n = newn;
    } while(n!=0)
    return a
  };

  // ECMAScript 5.1 Section 15.4.4.20
  Array.prototype.filter = function (callbackfn, thisArg) {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    var O = this;
    // 2. Let lenValue be the result of calling the [[Get]] internal method of O with the argument "length".
    var lenValue = O.length;
    // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
    var T;
    if(thisArg)
      T=thisArg;
    // 6. Let A be a new array created as if by the expression new Array() where Array is the standard built-in
    // constructor with that name.
    var A = new Array();
    // 7. Let k be 0.
    // 8. Let to be 0.
    var k = 0;
    var to = 0;
    // 9. Repeat, while k < len
    while (k < lenValue) {
       var kPresent = (k in O);
       if(kPresent) {
         var kValue = O[k];
         var selected = callbackfn.call(T, kValue, k, O);
         if(selected) {
            A[to] = kValue;
            to = to+1;
         }
       }
       k = k+1;
    }
    return A;
  };

})();

