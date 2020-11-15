QUnit.module('union methods');
lodashStable.each([
    'union',
    'unionBy',
    'unionWith'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return the union of two arrays', function (assert) {
        assert.expect(1);
        var actual = func([2], [
            1,
            2
        ]);
        assert.deepEqual(actual, [
            2,
            __num_top__
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return the union of multiple arrays', function (assert) {
        assert.expect(1);
        var actual = func([2], [
            1,
            2
        ], [
            2,
            3
        ]);
        assert.deepEqual(actual, [
            2,
            1,
            3
        ]);
    });
    QUnit.test('`_.' + methodName + '` should not flatten nested arrays', function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            3,
            2
        ], [
            1,
            [5]
        ], [
            2,
            [__num_top__]
        ]);
        assert.deepEqual(actual, [
            1,
            3,
            2,
            [5],
            [4]
        ]);
    });
    QUnit.test('`_.' + methodName + '` should ignore values that are not arrays or `arguments` objects', function (assert) {
        assert.expect(3);
        var array = [0];
        assert.deepEqual(func(array, 3, { '0': 1 }, null), array);
        assert.deepEqual(func(null, array, null, [
            __num_top__,
            1
        ]), [
            0,
            2,
            1
        ]);
        assert.deepEqual(func(array, null, args, null), [
            0,
            __num_top__,
            2,
            3
        ]);
    });
});