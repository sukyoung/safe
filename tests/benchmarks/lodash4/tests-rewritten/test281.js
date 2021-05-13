QUnit.module('values methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isValues = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '0': __str_top__,
                '1': __str_top__,
                'length': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            __num_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isValues ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var expected = isValues ? [__num_top__] : [
                __num_top__,
                __num_top__
            ], actual = func(new Foo()).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__,
                __num_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return func(value).sort();
        });
        assert.deepEqual(actual, expected);
    });
});