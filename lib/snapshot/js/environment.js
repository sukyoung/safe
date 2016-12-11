/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

var ___refArray  = [];
var ___refNameArray  = [
    ["window", "Global"],
    ["eval", "Global.eval"],
    ["parseInt", "Global.parseInt"],
    ["parseFloat", "Global.parseFloat"],
    ["isNaN", "Global.isNaN"],
    ["isFinite", "Global.isFinite"],
    ["decodeURI", "Global.decodeURI"],
    ["decodeURIComponent", "Global.decodeURIComponent"],
    ["encodeURI", "Global.encodeURI"],
    ["encodeURIComponent", "Global.encodeURIComponent"],
    ["JSON"],
    ["JSON.parse"],
    ["JSON.stringify"],
    ["Math"],
    ["Math.abs"],
    ["Math.acos"],
    ["Math.asin"],
    ["Math.atan"],
    ["Math.atan2"],
    ["Math.ceil"],
    ["Math.cos"],
    ["Math.exp"],
    ["Math.floor"],
    ["Math.log"],
    ["Math.max"],
    ["Math.min"],
    ["Math.pow"],
    ["Math.random"],
    ["Math.round"],
    ["Math.sin"],
    ["Math.sqrt"],
    ["Math.tan"],
    ["Number"],
    ["Number.prototype"],
    ["Number.prototype.constructor"],
    ["Number.prototype.toString"],
    ["Number.prototype.toLocaleString"],
    ["Number.prototype.valueOf"],
    ["Number.prototype.toFixed"],
    ["Number.prototype.toExponential"],
    ["Number.prototype.toPrecision"],
    ["Object"],
    ["Object.getPrototypeOf"],
    ["Object.getOwnPropertyDescriptor"],
    ["Object.getOwnPropertyNames"],
    ["Object.create"],
    ["Object.defineProperty"],
    ["Object.defineProperties"],
    ["Object.seal"],
    ["Object.freeze"],
    ["Object.preventExtensions"],
    ["Object.isSealed"],
    ["Object.isFrozen"],
    ["Object.isExtensible"],
    ["Object.keys"],
    ["Object.prototype"],
    ["Object.prototype.toString"],
    ["Object.prototype.toLocaleString"],
    ["Object.prototype.valueOf"],
    ["Object.prototype.hasOwnProperty"],
    ["Object.prototype.isPrototypeOf"],
    ["Object.prototype.propertyIsEnumerable"],
    ["RegExp"],
    ["RegExp.prototype"],
    ["RegExp.prototype.exec"],
    ["RegExp.prototype.test"],
    ["RegExp.prototype.toString"],
    ["String"],
    ["String.fromCharCode"],
    ["String.prototype"],
    ["String.prototype.constructor"],
    ["String.prototype.toString"],
    ["String.prototype.valueOf"],
    ["String.prototype.charAt"],
    ["String.prototype.charCodeAt"],
    ["String.prototype.concat"],
    ["String.prototype.indexOf"],
    ["String.prototype.lastIndexOf"],
    ["String.prototype.localeCompare"],
    ["String.prototype.match"],
    ["String.prototype.replace"],
    ["String.prototype.search"],
    ["String.prototype.slice"],
    ["String.prototype.split"],
    ["String.prototype.substring"],
    ["String.prototype.toLowerCase"],
    ["String.prototype.toLocaleLowerCase"],
    ["String.prototype.toUpperCase"],
    ["String.prototype.toLocaleUpperCase"],
    ["String.prototype.trim"],
    ["Array"],
    ["Array.isArray"],
    ["Array.prototype"],
    ["Array.prototype.toString"],
    ["Array.prototype.toLocaleString"],
    ["Array.prototype.concat"],
    ["Array.prototype.join"],
    ["Array.prototype.pop"],
    ["Array.prototype.push"],
    ["Array.prototype.reverse"],
    ["Array.prototype.shift"],
    ["Array.prototype.slice"],
    ["Array.prototype.sort"],
    ["Array.prototype.splice"],
    ["Array.prototype.unshift"],
    ["Array.prototype.indexOf"],
    ["Array.prototype.lastIndexOf"],
    ["Array.prototype.every"],
    ["Array.prototype.some"],
    ["Array.prototype.forEach"],
    ["Array.prototype.map"],
    ["Array.prototype.filter"],
    ["Array.prototype.reduce"],
    ["Array.prototype.reduceRight"],
    ["Boolean"],
    ["Boolean.prototype"],
    ["Boolean.prototype.toString"],
    ["Boolean.prototype.valueOf"],
    ["Date"],
    ["Date.parse"],
    ["Date.UTC"],
    ["Date.now"],
    ["Date.prototype"],
    ["Date.prototype.constructor"],
    ["Date.prototype.toString"],
    ["Date.prototype.toDateString"],
    ["Date.prototype.toTimeString"],
    ["Date.prototype.toLocaleString"],
    ["Date.prototype.toLocaleDateString"],
    ["Date.prototype.toLocaleTimeString"],
    ["Date.prototype.valueOf"],
    ["Date.prototype.getTime"],
    ["Date.prototype.getFullYear"],
    ["Date.prototype.getUTCFullYear"],
    ["Date.prototype.getMonth"],
    ["Date.prototype.getUTCMonth"],
    ["Date.prototype.getDate"],
    ["Date.prototype.getUTCDate"],
    ["Date.prototype.getDay"],
    ["Date.prototype.getUTCDay"],
    ["Date.prototype.getHours"],
    ["Date.prototype.getUTCHours"],
    ["Date.prototype.getMinutes"],
    ["Date.prototype.getUTCMinutes"],
    ["Date.prototype.getSeconds"],
    ["Date.prototype.getUTCSeconds"],
    ["Date.prototype.getMilliseconds"],
    ["Date.prototype.getUTCMilliseconds"],
    ["Date.prototype.getTimezoneOffset"],
    ["Date.prototype.setTime"],
    ["Date.prototype.setMilliseconds"],
    ["Date.prototype.setUTCMilliseconds"],
    ["Date.prototype.setSeconds"],
    ["Date.prototype.setUTCSeconds"],
    ["Date.prototype.setMinutes"],
    ["Date.prototype.setUTCMinutes"],
    ["Date.prototype.setHours"],
    ["Date.prototype.setUTCHours"],
    ["Date.prototype.setDate"],
    ["Date.prototype.setUTCDate"],
    ["Date.prototype.setMonth"],
    ["Date.prototype.setUTCMonth"],
    ["Date.prototype.setFullYear"],
    ["Date.prototype.setUTCFullYear"],
    ["Date.prototype.toUTCString"],
    ["Date.prototype.toISOString"],
    ["Date.prototype.toJSON"],
    ["Error"],
    ["Error.prototype"],
    ["Error.prototype.toString"],
    ["EvalError"],
    ["EvalError.prototype"],
    ["RangeError"],
    ["RangeError.prototype"],
    ["ReferenceError"],
    ["ReferenceError.prototype"],
    ["SyntaxError"],
    ["SyntaxError.prototype"],
    ["TypeError"],
    ["TypeError.prototype"],
    ["URIError"],
    ["URIError.prototype"],
    ["Function"],
    ["Function.prototype"],
    ["Function.prototype.toString"],
    ["Function.prototype.apply"],
    ["Function.prototype.call"],
    ["Function.prototype.bind"],
    ["Document"],
];

for (var i = 0; i < ___refNameArray.length; i++) {
    ___refArray.push(eval(___refNameArray[i][0]));
}

function ___saveDump(data, filename) {
    if (data === undefined) {
        console.error('No data');
        return;
    }

    if (filename === undefined) filename = 'dump.json'

    if (typeof data === "object") {
        data = JSON.stringify(data, undefined, "    ")
    }

    var blob = new Blob([data], {type: 'text/json'}),
        e    = document.createEvent('MouseEvents'),
        a    = document.createElement('a')

    a.download = filename
    a.href = window.URL.createObjectURL(blob)
    a.dataset.downloadurl = ['text/json', a.download, a.href].join(':')
    e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
    a.dispatchEvent(e)
}

function ___toJSON(name, value) {
    var interPS = "[[";
    var interPE = "]]";
    var refArray = [];
    var refNameArray = [];
    var locMap = {};
    function primitiveToJSON(value) {
        var ret;
        switch (typeof value) {
            case "undefined":
                ret = "@undef";
                break;
            case "boolean":
            case "string":
                ret = value;
                break;
            case "number":
                if (isNaN(value)) {
                    ret = "@NaN";
                } else if (value === Number.POSITIVE_INFINITY) {
                    ret = "@PosInf";
                } else if (value === Number.NEGATIVE_INFINITY) {
                    ret = "@NegInf";
                } else {
                    ret = value;
                }
                break;
            default:
                ret = ""
                break;
        }
        return ret;
    }
    function getRefID(name, value) {
        var i;
        for (i = 0; i < ___refNameArray.length; i++) {
            var ref = ___refArray[i];
            var modelName = ___refNameArray[i][1];
            if (modelName === undefined) modelName = ___refNameArray[i][0];
            if (ref === value) {
                return modelName;
            }
        }
        for (i = 0; i < refArray.length; i++) {
            if (refArray[i] === value) {
                return refNameArray[i];
            }
        }
        refArray.push(value);
        refNameArray.push(name);
        return refNameArray[i];
    }
    function objectToJSON(name, value) {
        var type = typeof value;
        if (value === null) return null;
        var ret = getRefID(name, value);
        name = ret;
        ret = "#" + ret;
        if (ret in locMap) {
            return ret;
        }
        var locObj = {};
        locMap[ret] = locObj;
        var props = Object.getOwnPropertyNames(value);
        for (var i = 0; i < props.length; i++) {
            var prop = props[i];
            locObj[prop] = descriptorToJSON(value, prop, name);
        }
        if (type === "function") {
            locObj[interPS + "Scope" + interPE] = null;
        }
        locObj[interPS + "Extensible" + interPE] = Object.isExtensible(value);
        locObj[interPS + "Prototype" + interPE] = valueToJSON(name + ".[[Prototype]]", Object.getPrototypeOf(value));

        var protoType = value.prototype;
        if (typeof protoType !== "undefined")
            locObj[interPS + "HasInstance" + interPE] = null;
        var interClass = interPS + "Class" + interPE;
        if (!(interClass in locObj)) {
            if (value === JSON) {
                locObj[interClass] = "JSON";
            } else if (value === Math) {
                locObj[interClass] = "Math";
            } else if (value === Object.prototype) {
                locObj[interClass] = "Object";
            } else if (value === Function.prototype) {
                locObj[interClass] = "Function";
            } else if (value === Date.prototype) {
                locObj[interClass] = "Function";
                locObj[interPS + "PrimitiveValue" + interPE] = "@NaN";
            } else if (value === Array.prototype) {
                locObj[interClass] = "Array";
            } else if (value === String.prototype) {
                locObj[interClass] = "String";
            } else if (value === Boolean.prototype) {
                locObj[interClass] = "Boolean";
            } else if (value === Number.prototype) {
                locObj[interClass] = "Number";
            } else if (value === RegExp.prototype) {
                locObj[interClass] = "RegExp";
            } else if (value === Error.prototype) {
                locObj[interClass] = "Error";
            } else if (value instanceof Number) {
                locObj[interClass] = "Number";
                locObj[interPS + "PrimitiveValue" + interPE] = primitiveToJSON(value.valueOf());
            } else if (value instanceof String) {
                locObj[interClass] = "String";
                locObj[interPS + "PrimitiveValue" + interPE] = primitiveToJSON(value.valueOf());
            } else if (value instanceof Boolean) {
                locObj[interClass] = "Boolean";
                locObj[interPS + "PrimitiveValue" + interPE] = primitiveToJSON(value.valueOf());
            } else if (value instanceof RegExp) {
                locObj[interClass] = "RegExp";
            } else if (value instanceof Date) {
                locObj[interClass] = "Date";
                locObj[interPS + "PrimitiveValue" + interPE] = primitiveToJSON(value.valueOf());
            } else if (value instanceof Error) {
                locObj[interClass] = "Error";
            } else if (value instanceof Array) {
                locObj[interClass] = "Array";
            } else if (value instanceof Function) {
                locObj[interClass] = "Function";
            } else {
                locObj[interClass] = "Object";
            }
        }

        return ret;
    }

    function getOwnPropValue(obj, prop) {
        if (prop === "__proto__") {
            return Object.getProptotypeOf(obj);
        }
        return obj[prop];
    }

    function getDescriptor(obj, prop) {
        try {
            var desc = Object.getOwnPropertyDescriptor(obj, prop);
            if (typeof desc === 'undefined') {
                var v = getOwnPropValue(obj, prop);
                return {'value':v, 'writable':false, 'enumerable':false, 'configurable':false};
            }
        } catch(e) {
            return {'value':undefined, 'writable':false, 'enumerable':false, 'configurable':false};
        }
        if (desc.hasOwnProperty('writable')) {
            return desc;
        } else {
            try {
                var v = getOwnPropValue(obj, prop);
            } catch(e) {
                v = undefined;
            }
            return {'value':v, 'writable':true, 'enumerable':desc.enumerable, 'configurable':desc.configurable};
        }
        return desc;
    }

    function valueToJSON(name, value) {
        switch (typeof value) {
            case "function":
            case "object":
                return objectToJSON(name, value);
                break;
            default:
                return primitiveToJSON(value);
                break;
        }
    }

    function descriptorToJSON(obj, prop, name) {
        var desc = getDescriptor(obj, prop);
        desc.value = valueToJSON(name + "." + prop, desc.value);
        return desc;
    }
    valueToJSON(name, value);
    return locMap;
}
