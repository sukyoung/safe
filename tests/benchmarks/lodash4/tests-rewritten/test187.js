QUnit.module('lodash.pick');
(function () {
    var args = toArgs([
            __str_top__,
            __str_top__
        ]), object = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__,
            'd': __num_top__
        }, nested = {
            'a': __num_top__,
            'b': {
                'c': __num_top__,
                'd': __num_top__
            }
        };
    QUnit.test('should flatten `paths`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.pick(object, __str_top__, __str_top__), {
            'a': __num_top__,
            'c': __num_top__
        });
        assert.deepEqual(_.pick(object, [
            __str_top__,
            __str_top__
        ], __str_top__), {
            'a': __num_top__,
            'c': __num_top__,
            'd': __num_top__
        });
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick(nested, __str_top__), { 'b': { 'c': __num_top__ } });
    });
    QUnit.test('should support path arrays', function (assert) {
        assert.expect(1);
        var object = {
                'a.b': __num_top__,
                'a': { 'b': __num_top__ }
            }, actual = _.pick(object, [[__str_top__]]);
        assert.deepEqual(actual, { 'a.b': __num_top__ });
    });
    QUnit.test('should pick a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': __num_top__,
            'a': { 'b': __num_top__ }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.deepEqual(_.pick(object, path), { 'a.b': __num_top__ });
        });
    });
    QUnit.test('should coerce `paths` to strings', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick({
            '0': __str_top__,
            '1': __str_top__
        }, __num_top__), { '0': __str_top__ });
    });
    QUnit.test('should return an empty object when `object` is nullish', function (assert) {
        assert.expect(2);
        lodashStable.each([
            null,
            undefined
        ], function (value) {
            assert.deepEqual(_.pick(value, __str_top__), {});
        });
    });
    QUnit.test('should work with a primitive `object`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick(__str_top__, __str_top__), { 'slice': __str_top__.slice });
    });
    QUnit.test('should work with `arguments` object `paths`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick(object, args), {
            'a': __num_top__,
            'c': __num_top__
        });
    });
}());