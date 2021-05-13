QUnit.module('lodash.flatMapDepth');

(function() {
  var array = [1, [2, [3, [4]], 5]];

  QUnit.test('should use a default `depth` of `1`', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.flatMapDepth(array, identity), [1, 2, [3, [4]], 5]);
  });

  QUnit.test('should use `_.identity` when `iteratee` is nullish', function(assert) {
    assert.expect(1);

    var values = [, null, undefined],
        expected = lodashStable.map(values, lodashStable.constant([1, 2, [3, [4]], 5]));

    var actual = lodashStable.map(values, function(value, index) {
      return index ? _.flatMapDepth(array, value) : _.flatMapDepth(array);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should treat a `depth` of < `1` as a shallow clone', function(assert) {
    assert.expect(2);

    lodashStable.each([-1, 0], function(depth) {
      assert.deepEqual(_.flatMapDepth(array, identity, depth), [1, [2, [3, [4]], 5]]);
    });
  });

  QUnit.test('should coerce `depth` to an integer', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.flatMapDepth(array, identity, 2.2), [1, 2, 3, [4], 5]);
  });
}());