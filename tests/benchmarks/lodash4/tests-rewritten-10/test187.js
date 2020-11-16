QUnit.module('lodash.pick');
(function () {
    var args = toArgs([
            'a',
            'c'
        ]), object = {
            'a': 1,
            'b': 2,
            'c': __num_top__,
            'd': 4
        }, nested = {
            'a': __num_top__,
            'b': {
                'c': 2,
                'd': 3
            }
        };
    QUnit.test('should flatten `paths`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.pick(object, 'a', 'c'), {
            'a': 1,
            'c': 3
        });
        assert.deepEqual(_.pick(object, [
            'a',
            'd'
        ], __str_top__), {
            'a': 1,
            'c': 3,
            'd': 4
        });
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick(nested, 'b.c'), { 'b': { 'c': 2 } });
    });
    QUnit.test('should support path arrays', function (assert) {
        assert.expect(1);
        var object = {
                'a.b': 1,
                'a': { 'b': 2 }
            }, actual = _.pick(object, [[__str_top__]]);
        assert.deepEqual(actual, { 'a.b': __num_top__ });
    });
    QUnit.test('should pick a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': 1,
            'a': { 'b': 2 }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.deepEqual(_.pick(object, path), { 'a.b': 1 });
        });
    });
    QUnit.test('should coerce `paths` to strings', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick({
            '0': __str_top__,
            '1': __str_top__
        }, 0), { '0': 'a' });
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
        assert.deepEqual(_.pick('', 'slice'), { 'slice': ''.slice });
    });
    QUnit.test('should work with `arguments` object `paths`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.pick(object, args), {
            'a': 1,
            'c': 3
        });
    });
}());