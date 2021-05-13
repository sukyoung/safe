QUnit.module('lodash.intersectionWith');

(function() {
  QUnit.test('should work with a `comparator`', function(assert) {
    assert.expect(1);

    var objects = [{ 'x': 1, 'y': 2 }, { 'x': 2, 'y': 1 }],
        others = [{ 'x': 1, 'y': 1 }, { 'x': 1, 'y': 2 }],
        actual = _.intersectionWith(objects, others, lodashStable.isEqual);

    assert.deepEqual(actual, [objects[0]]);
  });

  QUnit.test('should preserve the sign of `0`', function(assert) {
    assert.expect(1);

    var array = [-0],
        largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubZero),
        others = [[0], largeArray],
        expected = lodashStable.map(others, lodashStable.constant(['-0']));

    var actual = lodashStable.map(others, function(other) {
      return lodashStable.map(_.intersectionWith(array, other, lodashStable.eq), lodashStable.toString);
    });

    assert.deepEqual(actual, expected);
  });
}());