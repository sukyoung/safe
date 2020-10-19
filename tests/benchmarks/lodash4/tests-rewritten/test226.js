QUnit.module('sortedIndexOf methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSortedIndexOf = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var sorted = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(func(sorted, __num_top__), isSortedIndexOf ? __num_top__ : __num_top__);
    });
});