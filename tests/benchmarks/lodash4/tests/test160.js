QUnit.module('lodash.min');

(function() {
  QUnit.test('should return the smallest value from a collection', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.min([1, 2, 3]), 1);
  });

  QUnit.test('should return `undefined` for empty collections', function(assert) {
    assert.expect(1);

    var values = falsey.concat([[]]),
        expected = lodashStable.map(values, noop);

    var actual = lodashStable.map(values, function(value, index) {
      try {
        return index ? _.min(value) : _.min();
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should work with non-numeric collection values', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.min(['a', 'b']), 'a');
  });
}());