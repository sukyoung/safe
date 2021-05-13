QUnit.module('lodash.sumBy');
(function () {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], objects = [
            { 'a': 2 },
            { 'a': 3 },
            { 'a': __num_top__ }
        ];
    QUnit.test('should work with an `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.sumBy(objects, function (object) {
            return object.a;
        });
        assert.deepEqual(actual, __num_top__);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.sumBy(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [__num_top__]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var arrays = [
            [2],
            [3],
            [__num_top__]
        ];
        assert.strictEqual(_.sumBy(arrays, 0), __num_top__);
        assert.strictEqual(_.sumBy(objects, __str_top__), __num_top__);
    });
}());