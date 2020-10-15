QUnit.module('lodash.eq');

(function() {
  QUnit.test('should perform a `SameValueZero` comparison of two values', function(assert) {
    assert.expect(11);

    assert.strictEqual(_.eq(), true);
    assert.strictEqual(_.eq(undefined), true);
    assert.strictEqual(_.eq(0, -0), true);
    assert.strictEqual(_.eq(NaN, NaN), true);
    assert.strictEqual(_.eq(1, 1), true);

    assert.strictEqual(_.eq(null, undefined), false);
    assert.strictEqual(_.eq(1, Object(1)), false);
    assert.strictEqual(_.eq(1, '1'), false);
    assert.strictEqual(_.eq(1, '1'), false);

    var object = { 'a': 1 };
    assert.strictEqual(_.eq(object, object), true);
    assert.strictEqual(_.eq(object, { 'a': 1 }), false);
  });
}());