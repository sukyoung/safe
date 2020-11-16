QUnit.module('lodash.find and lodash.findLast');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var isFind = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var findCount = __num_top__, mapCount = __num_top__, array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__), iteratee = function (value) {
                    mapCount++;
                    return square(value);
                }, predicate = function (value) {
                    findCount++;
                    return isEven(value);
                }, actual = _(array).map(iteratee)[methodName](predicate);
            assert.strictEqual(findCount, isFind ? __num_top__ : __num_top__);
            assert.strictEqual(mapCount, isFind ? __num_top__ : __num_top__);
            assert.strictEqual(actual, isFind ? __num_top__ : square(LARGE_ARRAY_SIZE));
        } else {
            skipAssert(assert, 3);
        }
    });
});