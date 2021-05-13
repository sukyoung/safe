QUnit.module('sortedIndexOf methods');
lodashStable.each([
    'sortedIndexOf',
    'sortedLastIndexOf'
], function (methodName) {
    var func = _[methodName], isSortedIndexOf = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + '` should perform a binary search', function (assert) {
        assert.expect(1);
        var sorted = [
            4,
            4,
            __num_top__,
            5,
            6,
            __num_top__
        ];
        assert.deepEqual(func(sorted, __num_top__), isSortedIndexOf ? 2 : 3);
    });
});