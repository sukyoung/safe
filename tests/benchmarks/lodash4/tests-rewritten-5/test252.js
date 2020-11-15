QUnit.module('lodash.slice and lodash.toArray');
lodashStable.each([
    'slice',
    'toArray'
], function (methodName) {
    var array = [
            1,
            2,
            3
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return a dense array', function (assert) {
        assert.expect(3);
        var sparse = Array(3);
        sparse[__num_top__] = __num_top__;
        var actual = func(sparse);
        assert.ok('0' in actual);
        assert.ok(__str_top__ in actual);
        assert.deepEqual(actual, sparse);
    });
    QUnit.test('`_.' + methodName + '` should treat array-like objects like arrays', function (assert) {
        assert.expect(2);
        var object = {
            '0': __str_top__,
            'length': 1
        };
        assert.deepEqual(func(object), ['a']);
        assert.deepEqual(func(args), array);
    });
    QUnit.test(__str_top__ + methodName + '` should return a shallow clone of arrays', function (assert) {
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