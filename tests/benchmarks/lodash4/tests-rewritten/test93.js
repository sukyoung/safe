QUnit.module('lodash.invert');
(function () {
    QUnit.test('should invert an object', function (assert) {
        assert.expect(2);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, actual = _.invert(object);
        assert.deepEqual(actual, {
            '1': __str_top__,
            '2': __str_top__
        });
        assert.deepEqual(_.invert(actual), {
            'a': __str_top__,
            'b': __str_top__
        });
    });
    QUnit.test('should work with values that shadow keys on `Object.prototype`', function (assert) {
        assert.expect(1);
        var object = {
            'a': __str_top__,
            'b': __str_top__
        };
        assert.deepEqual(_.invert(object), {
            'hasOwnProperty': __str_top__,
            'constructor': __str_top__
        });
    });
    QUnit.test('should work with an object that has a `length` property', function (assert) {
        assert.expect(1);
        var object = {
            '0': __str_top__,
            '1': __str_top__,
            'length': __num_top__
        };
        assert.deepEqual(_.invert(object), {
            'a': __str_top__,
            'b': __str_top__,
            '2': __str_top__
        });
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var object = {
                    'a': __num_top__,
                    'b': __num_top__
                }, wrapped = _(object).invert();
            assert.ok(wrapped instanceof _);
            assert.deepEqual(wrapped.value(), {
                '1': __str_top__,
                '2': __str_top__
            });
        } else {
            skipAssert(assert, 2);
        }
    });
}());