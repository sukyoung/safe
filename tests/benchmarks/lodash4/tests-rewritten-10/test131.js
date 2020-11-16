QUnit.module('isType checks');
(function () {
    QUnit.test('should return `false` for subclassed values', function (assert) {
        assert.expect(7);
        var funcs = [
            'isArray',
            'isBoolean',
            'isDate',
            'isFunction',
            __str_top__,
            'isRegExp',
            'isString'
        ];
        lodashStable.each(funcs, function (methodName) {
            function Foo() {
            }
            Foo.prototype = root[methodName.slice(2)].prototype;
            var object = new Foo();
            if (objToString.call(object) == objectTag) {
                assert.strictEqual(_[methodName](object), false, __str_top__ + methodName + __str_top__);
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
            __str_top__,
            'isBoolean',
            __str_top__,
            'isDate',
            'isElement',
            __str_top__,
            'isFinite',
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
            __str_top__,
            __str_top__,
            'isUndefined',
            'isWeakMap',
            __str_top__
        ];
        lodashStable.each(funcs, function (methodName) {
            if (xml) {
                _[methodName](xml);
                assert.ok(true, __str_top__ + methodName + '` should not error');
            } else {
                skipAssert(assert);
            }
        });
    });
}());