QUnit.module('isType checks');
(function () {
    QUnit.test('should return `false` for subclassed values', function (assert) {
        assert.expect(7);
        var funcs = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        lodashStable.each(funcs, function (methodName) {
            function Foo() {
            }
            Foo.prototype = root[methodName.slice(__num_top__)].prototype;
            var object = new Foo();
            if (objToString.call(object) == objectTag) {
                assert.strictEqual(_[methodName](object), __bool_top__, __str_top__ + methodName + __str_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should not error on host objects (test in IE)', function (assert) {
        assert.expect(26);
        var funcs = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        lodashStable.each(funcs, function (methodName) {
            if (xml) {
                _[methodName](xml);
                assert.ok(__bool_top__, __str_top__ + methodName + __str_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
}());