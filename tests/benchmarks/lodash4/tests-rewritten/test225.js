QUnit.module('sortedIndexBy methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSortedIndexBy = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var args;
        func([
            __num_top__,
            __num_top__
        ], __num_top__, function (assert) {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var objects = [
                { 'x': __num_top__ },
                { 'x': __num_top__ }
            ], actual = func(objects, { 'x': __num_top__ }, __str_top__);
        assert.strictEqual(actual, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        var objects = [], iteratee = function () {
                throw new Error();
            }, actual = func(objects, { 'x': __num_top__ }, iteratee);
        assert.strictEqual(actual, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(12);
        lodashStable.each([
            Math.ceil(MAX_ARRAY_LENGTH / __num_top__),
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
                var expected = (isSortedIndexBy ? !lodashStable.isNaN(value) : lodashStable.isFinite(value)) ? __num_top__ : Math.min(length, MAX_ARRAY_INDEX);
                assert.ok(steps == __num_top__ || steps == __num_top__);
                assert.strictEqual(actual, expected);
            });
        });
    });
});