QUnit.module('union methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([__num_top__], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([__num_top__], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            [__num_top__]
        ], [
            __num_top__,
            [__num_top__]
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            [__num_top__],
            [__num_top__]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [__num_top__];
        assert.deepEqual(func(array, __num_top__, { '0': __num_top__ }, null), array);
        assert.deepEqual(func(null, array, null, [
            __num_top__,
            __num_top__
        ]), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(func(array, null, args, null), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
});