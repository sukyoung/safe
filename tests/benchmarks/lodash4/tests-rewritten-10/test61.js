QUnit.module('lodash.find and lodash.findLast');
lodashStable.each([
    'find',
    'findLast'
], function (methodName) {
    var isFind = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var findCount = __num_top__, mapCount = __num_top__, array = lodashStable.range(1, LARGE_ARRAY_SIZE + __num_top__), iteratee = function (value) {
                    mapCount++;
                    return square(value);
                }, predicate = function (value) {
                    findCount++;
                    return isEven(value);
                }, actual = _(array).map(iteratee)[methodName](predicate);
            assert.strictEqual(findCount, isFind ? __num_top__ : 1);
            assert.strictEqual(mapCount, isFind ? __num_top__ : __num_top__);
            assert.strictEqual(actual, isFind ? 4 : square(LARGE_ARRAY_SIZE));
        } else {
            skipAssert(assert, __num_top__);
        }
    });
});