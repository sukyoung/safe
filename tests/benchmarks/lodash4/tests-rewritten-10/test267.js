QUnit.module('union methods');
lodashStable.each([
    'union',
    __str_top__,
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
            1
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return the union of multiple arrays', function (assert) {
        assert.expect(1);
        var actual = func([__num_top__], [
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
            1,
            3,
            2
        ], [
            1,
            [5]
        ], [
            2,
            [4]
        ]);
        assert.deepEqual(actual, [
            1,
            3,
            2,
            [5],
            [4]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [0];
        assert.deepEqual(func(array, 3, { '0': __num_top__ }, null), array);
        assert.deepEqual(func(null, array, null, [
            2,
            1
        ]), [
            0,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(func(array, null, args, null), [
            __num_top__,
            __num_top__,
            2,
            3
        ]);
    });
});