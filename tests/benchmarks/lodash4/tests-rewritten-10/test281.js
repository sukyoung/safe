QUnit.module('values methods');
lodashStable.each([
    'values',
    'valuesIn'
], function (methodName) {
    var func = _[methodName], isValues = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should get string keyed values of `object`', function (assert) {
        assert.expect(1);
        var object = {
                'a': 1,
                'b': 2
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            __num_top__,
            2
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '0': __str_top__,
                '1': 'b',
                'length': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            2,
            __str_top__,
            'b'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isValues ? 'not ' : '') + 'include inherited string keyed property values', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var expected = isValues ? [1] : [
                1,
                __num_top__
            ], actual = func(new Foo()).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + '` should work with `arguments` objects', function (assert) {
        assert.expect(1);
        var values = [
                args,
                strictArgs
            ], expected = lodashStable.map(values, lodashStable.constant([
                1,
                2,
                __num_top__
            ]));
        var actual = lodashStable.map(values, function (value) {
            return func(value).sort();
        });
        assert.deepEqual(actual, expected);
    });
});