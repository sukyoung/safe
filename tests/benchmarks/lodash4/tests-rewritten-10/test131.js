QUnit.module('isType checks');
(function () {
    QUnit.test('should return `false` for subclassed values', function (assert) {
        assert.expect(7);
        var funcs = [
            'isArray',
            __str_top__,
            'isDate',
            __str_top__,
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
                assert.strictEqual(_[methodName](object), __bool_top__, '`_.' + methodName + __str_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should not error on host objects (test in IE)', function (assert) {
        assert.expect(26);
        var funcs = [
            __str_top__,
            'isArray',
            __str_top__,
            'isArrayLike',
            'isBoolean',
            'isBuffer',
            __str_top__,
            __str_top__,
            'isError',
            'isFinite',
            'isFunction',
            'isInteger',
            __str_top__,
            __str_top__,
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