QUnit.module('lodash.isLength');

(function() {
  QUnit.test('should return `true` for lengths', function(assert) {
    assert.expect(1);

    var values = [0, 3, MAX_SAFE_INTEGER],
        expected = lodashStable.map(values, stubTrue),
        actual = lodashStable.map(values, _.isLength);

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should return `false` for non-lengths', function(assert) {
    assert.expect(1);

    var values = [-1, '1', 1.1, MAX_SAFE_INTEGER + 1],
        expected = lodashStable.map(values, stubFalse),
        actual = lodashStable.map(values, _.isLength);

    assert.deepEqual(actual, expected);
  });
}());