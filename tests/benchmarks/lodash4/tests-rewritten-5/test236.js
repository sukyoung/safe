QUnit.module('lodash.sumBy');
(function () {
    var array = [
            6,
            __num_top__,
            2
        ], objects = [
            { 'a': 2 },
            { 'a': __num_top__ },
            { 'a': 1 }
        ];
    QUnit.test('should work with an `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.sumBy(objects, function (object) {
            return object.a;
        });
        assert.deepEqual(actual, 6);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.sumBy(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [6]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var arrays = [
            [2],
            [__num_top__],
            [1]
        ];
        assert.strictEqual(_.sumBy(arrays, __num_top__), __num_top__);
        assert.strictEqual(_.sumBy(objects, 'a'), 6);
    });
}());