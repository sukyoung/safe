QUnit.module('math operator methods');
lodashStable.each([
    'add',
    'divide',
    __str_top__,
    'subtract'
], function (methodName) {
    var func = _[methodName], isAddSub = methodName == 'add' || methodName == 'subtract';
    QUnit.test('`_.' + methodName + '` should return `' + (isAddSub ? __num_top__ : 1) + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(), isAddSub ? 0 : 1);
    });
    QUnit.test('`_.' + methodName + '` should work with only one defined argument', function (assert) {
        assert.expect(3);
        assert.strictEqual(func(6), 6);
        assert.strictEqual(func(6, undefined), 6);
        assert.strictEqual(func(undefined, 4), 4);
    });
    QUnit.test(__str_top__ + methodName + '` should preserve the sign of `0`', function (assert) {
        assert.expect(2);
        var values = [
                __num_top__,
                '0',
                -__num_top__,
                '-0'
            ], expected = [
                [
                    0,
                    Infinity
                ],
                [
                    '0',
                    Infinity
                ],
                [
                    -0,
                    -Infinity
                ],
                [
                    '-0',
                    -Infinity
                ]
            ];
        lodashStable.times(2, function (index) {
            var actual = lodashStable.map(values, function (value) {
                var result = index ? func(undefined, value) : func(value);
                return [
                    result,
                    1 / result
                ];
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should convert objects to `NaN`', function (assert) {
        assert.expect(2);
        assert.deepEqual(func(0, {}), NaN);
        assert.deepEqual(func({}, __num_top__), NaN);
    });
    QUnit.test('`_.' + methodName + '` should convert symbols to `NaN`', function (assert) {
        assert.expect(2);
        if (Symbol) {
            assert.deepEqual(func(__num_top__, symbol), NaN);
            assert.deepEqual(func(symbol, 0), NaN);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + '` should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _(__num_top__)[methodName](2);
            assert.notOk(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _(1).chain()[methodName](2);
            assert.ok(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});