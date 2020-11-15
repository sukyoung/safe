QUnit.module('sortedIndexOf methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSortedIndexOf = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should perform a binary search', function (assert) {
        assert.expect(1);
        var sorted = [
            4,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(func(sorted, 5), isSortedIndexOf ? __num_top__ : __num_top__);
    });
});