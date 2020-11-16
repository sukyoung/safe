QUnit.module('lodash.slice and lodash.toArray');
lodashStable.each([
    'slice',
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            2,
            __num_top__
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return a dense array', function (assert) {
        assert.expect(3);
        var sparse = Array(3);
        sparse[1] = 2;
        var actual = func(sparse);
        assert.ok('0' in actual);
        assert.ok('2' in actual);
        assert.deepEqual(actual, sparse);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var object = {
            '0': 'a',
            'length': 1
        };
        assert.deepEqual(func(object), [__str_top__]);
        assert.deepEqual(func(args), array);
    });
    QUnit.test('`_.' + methodName + '` should return a shallow clone of arrays', function (assert) {
        assert.expect(2);
        var actual = func(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('`_.' + methodName + '` should work with a node list for `collection`', function (assert) {
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