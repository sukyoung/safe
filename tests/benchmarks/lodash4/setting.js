;(function() {
  // job queue
  this.jobs = {};
  var uidCount = 0;
  this.popJobs = function() {
    var count;
    do {
      count = 0;
      var curTime = __getTime__();
      for (var id in jobs) {
        var job = jobs[id];
        if (job.time <= curTime) {
          job.func();
          delete jobs[id];
        } else count++;
      }
    } while(count > 0);
  }

  // modeling setTimeout and clearTimeout
  this.setTimeout = function(f, duration) {
    var uid = uidCount++;
    jobs[uid] = {
      func: f,
      time: __getTime__() + duration
    };
    return uid;
  }
  this.clearTimeout = function(uid) { delete jobs[uid]; }

  // removed ES6 or Node.js features
  delete this.document;
  delete this.WeakMap;
  delete this.WeakSet;
  delete this.Map;
  delete this.Set;
  delete this.Symbol;
  delete this.DataView
  delete this.Promise
  delete this.Array.from;
  delete this.Object.getOwnPropertySymbols;
  delete this.exports;
  delete this.Buffer;
  delete this.process;
  delete this.ArrayBuffer;
  delete this.Proxy;

  // removed typed arrays
  delete this.Float32Array;
  delete this.Float64Array;
  delete this.Int8Array;
  delete this.Int16Array;
  delete this.Int32Array;
  delete this.Uint8Array;
  delete this.Uint8ClampedArray;
  delete this.Uint16Array;
  delete this.Uint32Array;

  // Set the print function
  if (!this.print) {
    if (console) this.print = console.log;
    else this.print = this.__print__;
  }

  // Modeling of Date
  var __time__ = 0;
  var __interval__ = 10;

  var origDate = this.Date;
  function Date(time) {
    if (time === undefined) time = __getTime__();
    return new origDate(time);
  }
  function __getTime__() { return __time__ += __interval__; }
  Date.now = __getTime__;
  this.Date = Date;

  var self = this;
  var origRandom = this.Math.random;
  function random() {
    try {
      return __num_top__;
    } catch(e) {
      return origRandom.call(this);
    }
  }
  this.Math.random = random;

  // Light modeling of QUnit test module
  var QUnit = {};
  QUnit.module = function module(name) { print('[MODULE] ' + name); }
  QUnit.test = function test(msg, f) {
    var assert = new Assert;
    f(assert);
    if (assert.isAsync) popJobs();
    else assert.check();
  }
  this.QUnit = QUnit;

  function fail(msg) {
    print('[FAIL] ' + msg); // throw new Error(msg);
  }

  function Assert() { this.isAsync = false; }
  Assert.prototype.expect = function(n) { this.count = n; };
  Assert.prototype.ok = function ok(pass, msg) {
    if (pass) {
      print('[PASS] ' + msg);
      this.count--;
    } else fail(msg);
  }
  Assert.prototype.notOk = function(fail, msg) {
    this.ok(!fail, msg);
  }
  Assert.prototype.deepEqual = function(a, b) {
    this.ok(equiv(a, b), 'deepEqual');
  }
  Assert.prototype.equal = function(a, b) {
    this.ok(a == b, 'equal');
  }
  Assert.prototype.notEqual = function(a, b) {
    this.ok(a != b, 'notEqual');
  }
  Assert.prototype.strictEqual = function(a, b) {
    this.ok(a === b, 'strictEqual');
  }
  Assert.prototype.notStrictEqual = function(a, b) {
    this.ok(a !== b, 'notStrictEqual');
  }
  Assert.prototype.raises = function (f, expected) {
    var pass = false;
    try { f(); } catch(e) { pass = true; }
    this.ok(pass, 'raises');
  }
  Assert.prototype.async = function () {
    var self = this;
    this.isAsync = true;
    return function () { self.check(); };
  }
  Assert.prototype.check = function () {
    if (this.count > 0) fail(this.count + ' assertions remain');
  }

  function _typeof(obj) {
    "@babel/helpers - typeof";

    if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") {
      _typeof = function (obj) {
        return typeof obj;
      };
    } else {
      _typeof = function (obj) {
        return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
      };
    }

    return _typeof(obj);
  }
  function objectType(obj) {
    if (typeof obj === "undefined") return "undefined";
    if (obj === null) return "null";
    var str = toString.call(obj);
    if (str.substring(0, 8) === '[object ') {
      var type = str.substring(8, str.length - 1);
    }
    switch (type) {
      case "Number":
        if (isNaN(obj)) return "nan";
        return "number";
      case "String":
      case "Boolean":
      case "Array":
      case "Set":
      case "Map":
      case "Date":
      case "RegExp":
      case "Function":
      case "Symbol": return type.toLowerCase();
      default: return _typeof(obj);
    }
  }
  var equiv = (function () {
    var pairs = [];
    var getProto = Object.getPrototypeOf || function (obj) {
      return obj.__proto__;
    };
    function useStrictEquality(a, b) {
      if (_typeof(a) === "object") a = a.valueOf();
      if (_typeof(b) === "object") b = b.valueOf();
      return a === b;
    }
    function compareConstructors(a, b) {
      var protoA = getProto(a);
      var protoB = getProto(b);
      if (a.constructor === b.constructor) return true;
      if (protoA && protoA.constructor === null) protoA = null;
      if (protoB && protoB.constructor === null) protoB = null;
      if (protoA === null && protoB === Object.prototype || protoB === null && protoA === Object.prototype) return true;
      return false;
    }
    function getRegExpFlags(regexp) {
      return "flags" in regexp ? regexp.flags : regexp.toString().match(/[gimuy]*$/)[0];
    }
    function isContainer(val) {
      return ["object", "array", "map", "set"].indexOf(objectType(val)) !== -1;
    }
    function breadthFirstCompareChild(a, b) {
      if (a === b) return true;
      if (!isContainer(a)) return typeEquiv(a, b);
      if (pairs.every(function (pair) {
        return pair.a !== a || pair.b !== b;
      })) {
        pairs.push({
          a: a,
          b: b
        });
      }
      return true;
    }
    var callbacks = {
      "string": useStrictEquality,
      "boolean": useStrictEquality,
      "number": useStrictEquality,
      "null": useStrictEquality,
      "undefined": useStrictEquality,
      "symbol": useStrictEquality,
      "date": useStrictEquality,
      "nan": function nan() {
        return true;
      },
      "regexp": function regexp(a, b) {
        return a.source === b.source &&
        getRegExpFlags(a) === getRegExpFlags(b);
      },
      "function": function _function() {
        return false;
      },
      "array": function array(a, b) {
        var i, len;
        len = a.length;
        if (len !== b.length) {
          return false;
        }
        for (i = 0; i < len; i++) {
          if (!breadthFirstCompareChild(a[i], b[i])) {
            return false;
          }
        }

        return true;
      },
      "set": function set(a, b) {
        var innerEq,
            outerEq = true;
        if (a.size !== b.size) {
          return false;
        }
        a.forEach(function (aVal) {
          if (!outerEq) return;
          innerEq = false;
          b.forEach(function (bVal) {
            var parentPairs;
            if (innerEq) return;
            parentPairs = pairs;
            if (innerEquiv(bVal, aVal)) innerEq = true;
            pairs = parentPairs;
          });
          if (!innerEq) {
            outerEq = false;
          }
        });
        return outerEq;
      },
      "map": function map(a, b) {
        var innerEq,
            outerEq = true;
        if (a.size !== b.size) return false;
        a.forEach(function (aVal, aKey) {
          if (!outerEq) return;
          innerEq = false;
          b.forEach(function (bVal, bKey) {
            var parentPairs;
            if (innerEq) return;
            parentPairs = pairs;
            if (innerEquiv([bVal, bKey], [aVal, aKey])) innerEq = true;
            pairs = parentPairs;
          });
          if (!innerEq) outerEq = false;
        });
        return outerEq;
      },
      "object": function object(a, b) {
        var i,
            aProperties = [],
            bProperties = [];
        if (compareConstructors(a, b) === false) return false;
        for (i in a) {
          aProperties.push(i);
          if (a.constructor !== Object && typeof a.constructor !== "undefined" && typeof a[i] === "function" && typeof b[i] === "function" && a[i].toString() === b[i].toString()) continue;
          if (!breadthFirstCompareChild(a[i], b[i])) return false;
        }
        for (i in b) {
          bProperties.push(i);
        }
        return typeEquiv(aProperties.sort(), bProperties.sort());
      }
    };
    function typeEquiv(a, b) {
      var type = objectType(a);
      return objectType(b) === type && callbacks[type](a, b);
    }
    function innerEquiv(a, b) {
      var i, pair;
      if (arguments.length < 2) return true;
      pairs = [{
        a: a,
        b: b
      }];
      for (i = 0; i < pairs.length; i++) {
        pair = pairs[i];
        if (pair.a !== pair.b && !typeEquiv(pair.a, pair.b)) {
          return false;
        }
      }
      return arguments.length === 2 || innerEquiv.apply(this, [].slice.call(arguments, 1));
    }
    return function () {
      var result = innerEquiv.apply(void 0, arguments);
      pairs.length = 0;
      return result;
    };
  })();
}).call(global);
