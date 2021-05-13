QUnit.module('isType checks');
(function () {
    QUnit.test('should return `false` for subclassed values', function (assert) {
        assert.expect(7);
        var funcs = [
            'isArray',
            'isBoolean',
            'isDate',
            'isFunction',
            'isNumber',
            'isRegExp',
            'isString'
        ];
        lodashStable.each(funcs, function (methodName) {
            function Foo() {
            }
            Foo.prototype = root[methodName.slice(2)].prototype;
            var object = new Foo();
            if (objToString.call(object) == objectTag) {
                assert.strictEqual(_[methodName](object), false, '`_.' + methodName + '` returns `false`');
            } else {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should not error on host objects (test in IE)', function (assert) {
        assert.expect(26);
        var funcs = [
            'isArguments',
            'isArray',
            'isArrayBuffer',
            'isArrayLike',
            'isBoolean',
            'isBuffer',
            'isDate',
            'isElement',
            'isError',
            __str_top__,
            'isFunction',
            'isInteger',
            'isMap',
            'isNaN',
            'isNil',
            'isNull',
            'isNumber',
            'isObject',
            'isObjectLike',
            'isRegExp',
            'isSet',
            'isSafeInteger',
            'isString',
            'isUndefined',
            'isWeakMap',
            'isWeakSet'
        ];
        lodashStable.each(funcs, function (methodName) {
            if (xml) {
                _[methodName](xml);
                assert.ok(true, '`_.' + methodName + '` should not error');
            } else {
                skipAssert(assert);
            }
        });
    });
}());