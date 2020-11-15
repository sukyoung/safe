QUnit.module('values methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isValues = methodName == __str_top__;
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                'a': 1,
                'b': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            1,
            2
        ]);
    });
    QUnit.test('`_.' + methodName + '` should work with an object that has a `length` property', function (assert) {
        assert.expect(1);
        var object = {
                '0': 'a',
                '1': 'b',
                'length': __num_top__
            }, actual = func(object).sort();
        assert.deepEqual(actual, [
            __num_top__,
            'a',
            'b'
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ' + (isValues ? 'not ' : '') + 'include inherited string keyed property values', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var expected = isValues ? [1] : [
                1,
                2
            ], actual = func(new Foo()).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with `arguments` objects', function (assert) {
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