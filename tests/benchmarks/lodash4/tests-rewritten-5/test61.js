QUnit.module('lodash.find and lodash.findLast');
lodashStable.each([
    __str_top__,
    'findLast'
], function (methodName) {
    var isFind = methodName == 'find';
    QUnit.test('`_.' + methodName + '` should support shortcut fusion', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var findCount = __num_top__, mapCount = __num_top__, array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + 1), iteratee = function (value) {
                    mapCount++;
                    return square(value);
                }, predicate = function (value) {
                    findCount++;
                    return isEven(value);
                }, actual = _(array).map(iteratee)[methodName](predicate);
            assert.strictEqual(findCount, isFind ? 2 : 1);
            assert.strictEqual(mapCount, isFind ? 2 : 1);
            assert.strictEqual(actual, isFind ? 4 : square(LARGE_ARRAY_SIZE));
        } else {
            skipAssert(assert, __num_top__);
        }
    });
});