QUnit.module('indexOf methods');
lodashStable.each([
    'indexOf',
    'lastIndexOf',
    'sortedIndexOf',
    'sortedLastIndexOf'
], function (methodName) {
    var func = _[methodName], isIndexOf = !/last/i.test(methodName), isSorted = /^sorted/.test(methodName);
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(-__num_top__));
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? func(array) : func();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `-1` for an unmatched value', function (assert) {
        assert.expect(5);
        var array = [
                __num_top__,
                2,
                3
            ], empty = [];
        assert.strictEqual(func(array, 4), -1);
        assert.strictEqual(func(array, 4, true), -1);
        assert.strictEqual(func(array, undefined, __bool_top__), -1);
        assert.strictEqual(func(empty, undefined), -1);
        assert.strictEqual(func(empty, undefined, true), -1);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [];
        array[-1] = __num_top__;
        assert.strictEqual(func(array, undefined), -1);
        assert.strictEqual(func(array, 0, true), -1);
    });
    QUnit.test('`_.' + methodName + '` should match `NaN`', function (assert) {
        assert.expect(3);
        var array = isSorted ? [
            __num_top__,
            2,
            NaN,
            NaN
        ] : [
            1,
            NaN,
            __num_top__,
            NaN,
            5,
            NaN
        ];
        if (isSorted) {
            assert.strictEqual(func(array, NaN, true), isIndexOf ? 2 : 3);
            skipAssert(assert, 2);
        } else {
            assert.strictEqual(func(array, NaN), isIndexOf ? 1 : 5);
            assert.strictEqual(func(array, NaN, 2), isIndexOf ? 3 : 1);
            assert.strictEqual(func(array, NaN, -2), isIndexOf ? 5 : 3);
        }
    });
    QUnit.test('`_.' + methodName + '` should match `-0` as `0`', function (assert) {
        assert.expect(2);
        assert.strictEqual(func([-0], 0), __num_top__);
        assert.strictEqual(func([0], -0), __num_top__);
    });
});