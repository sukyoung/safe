QUnit.module('sortedIndexOf methods');
lodashStable.each([
    __str_top__,
    'sortedLastIndexOf'
], function (methodName) {
    var func = _[methodName], isSortedIndexOf = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should perform a binary search', function (assert) {
        assert.expect(1);
        var sorted = [
            __num_top__,
            4,
            5,
            __num_top__,
            __num_top__,
            6
        ];
        assert.deepEqual(func(sorted, 5), isSortedIndexOf ? 2 : 3);
    });
});