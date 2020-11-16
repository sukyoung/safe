QUnit.module('lodash.slice and lodash.toArray');
lodashStable.each([
    'slice',
    'toArray'
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            3
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var sparse = Array(3);
        sparse[__num_top__] = 2;
        var actual = func(sparse);
        assert.ok('0' in actual);
        assert.ok(__str_top__ in actual);
        assert.deepEqual(actual, sparse);
    });
    QUnit.test('`_.' + methodName + '` should treat array-like objects like arrays', function (assert) {
        assert.expect(2);
        var object = {
            '0': __str_top__,
            'length': __num_top__
        };
        assert.deepEqual(func(object), [__str_top__]);
        assert.deepEqual(func(args), array);
    });
    QUnit.test(__str_top__ + methodName + '` should return a shallow clone of arrays', function (assert) {
        assert.expect(2);
        var actual = func(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test(__str_top__ + methodName + '` should work with a node list for `collection`', function (assert) {
        assert.expect(1);
        if (document) {
            try {
                var actual = func(document.getElementsByTagName('body'));
            } catch (e) {
            }
            assert.deepEqual(actual, [body]);
        } else {
            skipAssert(assert);
        }
    });
});