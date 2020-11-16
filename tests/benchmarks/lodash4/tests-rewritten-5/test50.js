QUnit.module('lodash.dropRightWhile');
(function () {
    var array = [
        1,
        2,
        3,
        4
    ];
    var objects = [
        {
            'a': 0,
            'b': 0
        },
        {
            'a': 1,
            'b': __num_top__
        },
        {
            'a': __num_top__,
            'b': 2
        }
    ];
    QUnit.test('should drop elements while `predicate` returns truthy', function (assert) {
        assert.expect(1);
        var actual = _.dropRightWhile(array, function (n) {
            return n > 2;
        });
        assert.deepEqual(actual, [
            1,
            2
        ]);
    });
    QUnit.test('should provide correct `predicate` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.dropRightWhile(array, function () {
            args = slice.call(arguments);
        });
        assert.deepEqual(args, [
            4,
            __num_top__,
            array
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropRightWhile(objects, { 'b': 2 }), objects.slice(0, 2));
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropRightWhile(objects, [
            'b',
            __num_top__
        ]), objects.slice(0, 2));
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.dropRightWhile(objects, 'b'), objects.slice(0, 1));
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array).dropRightWhile(function (n) {
                return n > 2;
            });
            assert.ok(wrapped instanceof _);
            assert.deepEqual(wrapped.value(), [
                1,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());