QUnit.module('lodash.subtract');

(function() {
  QUnit.test('should subtract two numbers', function(assert) {
    assert.expect(3);

    assert.strictEqual(_.subtract(6, 4), 2);
    assert.strictEqual(_.subtract(-6, 4), -10);
    assert.strictEqual(_.subtract(-6, -4), -2);
  });

  QUnit.test('should coerce arguments to numbers', function(assert) {
    assert.expect(2);

    assert.strictEqual(_.subtract('6', '4'), 2);
    assert.deepEqual(_.subtract('x', 'y'), NaN);
  });
}());