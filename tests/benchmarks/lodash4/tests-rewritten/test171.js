QUnit.module('lodash.omit');
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
        assert.deepEqual(_.omit(object, __str_top__, __str_top__), {
            'b': __num_top__,
            'd': __num_top__
        });
        assert.deepEqual(_.omit(object, [
            __str_top__,
            __str_top__
        ], __str_top__), { 'b': __num_top__ });
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.omit(nested, __str_top__), {
            'a': __num_top__,
            'b': { 'd': __num_top__ }
        });
    });
    QUnit.test('should support path arrays', function (assert) {
        assert.expect(1);
        var object = {
                'a.b': __num_top__,
                'a': { 'b': __num_top__ }
            }, actual = _.omit(object, [[__str_top__]]);
        assert.deepEqual(actual, { 'a': { 'b': __num_top__ } });
    });
    QUnit.test('should omit a key over a path', function (assert) {
        assert.expect(2);
        var object = {
            'a.b': __num_top__,
            'a': { 'b': __num_top__ }
        };
        lodashStable.each([
            __str_top__,
            [__str_top__]
        ], function (path) {
            assert.deepEqual(_.omit(object, path), { 'a': { 'b': __num_top__ } });
        });
    });
    QUnit.test('should coerce `paths` to strings', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.omit({ '0': __str_top__ }, __num_top__), {});
    });
    QUnit.test('should return an empty object when `object` is nullish', function (assert) {
        assert.expect(2);
        lodashStable.each([
            null,
            undefined
        ], function (value) {
            objectProto.a = __num_top__;
            var actual = _.omit(value, __str_top__);
            delete objectProto.a;
            assert.deepEqual(actual, {});
        });
    });
    QUnit.test('should work with a primitive `object`', function (assert) {
        assert.expect(1);
        stringProto.a = __num_top__;
        stringProto.b = __num_top__;
        assert.deepEqual(_.omit(__str_top__, __str_top__), { 'a': __num_top__ });
        delete stringProto.a;
        delete stringProto.b;
    });
    QUnit.test('should work with `arguments` object `paths`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.omit(object, args), {
            'b': __num_top__,
            'd': __num_top__
        });
    });
    QUnit.test('should not mutate `object`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            [__str_top__],
            __str_top__,
            [__str_top__]
        ], function (path) {
            var object = { 'a': { 'b': __num_top__ } };
            _.omit(object, path);
            assert.deepEqual(object, { 'a': { 'b': __num_top__ } });
        });
    });
}());