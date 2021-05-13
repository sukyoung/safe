QUnit.module('lodash.find and lodash.findLast');
lodashStable.each([
    'find',
    'findLast'
], function (methodName) {
    var isFind = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should support shortcut fusion', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var findCount = 0, mapCount = __num_top__, array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + 1), iteratee = function (value) {
                    mapCount++;
                    return square(value);
                }, predicate = function (value) {
                    findCount++;
                    return isEven(value);
                }, actual = _(array).map(iteratee)[methodName](predicate);
            assert.strictEqual(findCount, isFind ? 2 : 1);
            assert.strictEqual(mapCount, isFind ? 2 : __num_top__);
            assert.strictEqual(actual, isFind ? __num_top__ : square(LARGE_ARRAY_SIZE));
        } else {
            skipAssert(assert, 3);
        }
    });
});