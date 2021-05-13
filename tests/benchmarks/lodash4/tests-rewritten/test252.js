QUnit.module('lodash.slice and lodash.toArray');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var sparse = Array(__num_top__);
        sparse[__num_top__] = __num_top__;
        var actual = func(sparse);
        assert.ok(__str_top__ in actual);
        assert.ok(__str_top__ in actual);
        assert.deepEqual(actual, sparse);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = {
            '0': __str_top__,
            'length': __num_top__
        };
        assert.deepEqual(func(object), [__str_top__]);
        assert.deepEqual(func(args), array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var actual = func(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (document) {
            try {
                var actual = func(document.getElementsByTagName(__str_top__));
            } catch (e) {
            }
            assert.deepEqual(actual, [body]);
        } else {
            skipAssert(assert);
        }
    });
});