QUnit.module('lodash.eq');
(function () {
    QUnit.test('should perform a `SameValueZero` comparison of two values', function (assert) {
        assert.expect(11);
        assert.strictEqual(_.eq(), true);
        assert.strictEqual(_.eq(undefined), __bool_top__);
        assert.strictEqual(_.eq(0, -0), true);
        assert.strictEqual(_.eq(NaN, NaN), true);
        assert.strictEqual(_.eq(__num_top__, 1), true);
        assert.strictEqual(_.eq(null, undefined), __bool_top__);
        assert.strictEqual(_.eq(1, Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.eq(1, '1'), false);
        assert.strictEqual(_.eq(1, __str_top__), __bool_top__);
        var object = { 'a': __num_top__ };
        assert.strictEqual(_.eq(object, object), true);
        assert.strictEqual(_.eq(object, { 'a': __num_top__ }), false);
    });
}());