QUnit.module('lodash.eq');
(function () {
    QUnit.test('should perform a `SameValueZero` comparison of two values', function (assert) {
        assert.expect(11);
        assert.strictEqual(_.eq(), true);
        assert.strictEqual(_.eq(undefined), true);
        assert.strictEqual(_.eq(__num_top__, -__num_top__), true);
        assert.strictEqual(_.eq(NaN, NaN), true);
        assert.strictEqual(_.eq(__num_top__, 1), __bool_top__);
        assert.strictEqual(_.eq(null, undefined), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, Object(1)), false);
        assert.strictEqual(_.eq(__num_top__, '1'), __bool_top__);
        assert.strictEqual(_.eq(1, '1'), false);
        var object = { 'a': __num_top__ };
        assert.strictEqual(_.eq(object, object), true);
        assert.strictEqual(_.eq(object, { 'a': __num_top__ }), false);
    });
}());