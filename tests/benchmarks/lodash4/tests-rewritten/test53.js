QUnit.module('lodash.eq');
(function () {
    QUnit.test('should perform a `SameValueZero` comparison of two values', function (assert) {
        assert.expect(11);
        assert.strictEqual(_.eq(), __bool_top__);
        assert.strictEqual(_.eq(undefined), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, -__num_top__), __bool_top__);
        assert.strictEqual(_.eq(NaN, NaN), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.eq(null, undefined), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, Object(__num_top__)), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, __str_top__), __bool_top__);
        assert.strictEqual(_.eq(__num_top__, __str_top__), __bool_top__);
        var object = { 'a': __num_top__ };
        assert.strictEqual(_.eq(object, object), __bool_top__);
        assert.strictEqual(_.eq(object, { 'a': __num_top__ }), __bool_top__);
    });
}());