QUnit.module('sortedIndexBy methods');
lodashStable.each([
    'sortedIndexBy',
    __str_top__
], function (methodName) {
    var func = _[methodName], isSortedIndexBy = methodName == 'sortedIndexBy';
    QUnit.test(__str_top__ + methodName + '` should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        func([
            __num_top__,
            50
        ], __num_top__, function (assert) {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [40]);
    });
    QUnit.test('`_.' + methodName + '` should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
                { 'x': __num_top__ },
                { 'x': 50 }
            ], actual = func(objects, { 'x': __num_top__ }, 'x');
        assert.strictEqual(actual, __num_top__);
    });
    QUnit.test('`_.' + methodName + '` should avoid calling iteratee when length is 0', function (assert) {
        var objects = [], iteratee = function () {
                throw new Error();
            }, actual = func(objects, { 'x': 50 }, iteratee);
        assert.strictEqual(actual, 0);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(12);
        lodashStable.each([
            Math.ceil(MAX_ARRAY_LENGTH / 2),
            MAX_ARRAY_LENGTH
        ], function (length) {
            var array = [], values = [
                    MAX_ARRAY_LENGTH,
                    NaN,
                    undefined
                ];
            array.length = length;
            lodashStable.each(values, function (value) {
                var steps = __num_top__;
                var actual = func(array, value, function (value) {
                    steps++;
                    return value;
                });
                var expected = (isSortedIndexBy ? !lodashStable.isNaN(value) : lodashStable.isFinite(value)) ? 0 : Math.min(length, MAX_ARRAY_INDEX);
                assert.ok(steps == 32 || steps == 33);
                assert.strictEqual(actual, expected);
            });
        });
    });
});