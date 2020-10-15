QUnit.module('lodash.differenceWith');

(function() {
  QUnit.test('should work with a `comparator`', function(assert) {
    assert.expect(1);

    var objects = [{ 'x': 1, 'y': 2 }, { 'x': 2, 'y': 1 }],
        actual = _.differenceWith(objects, [{ 'x': 1, 'y': 2 }], lodashStable.isEqual);

    assert.deepEqual(actual, [objects[1]]);
  });

  QUnit.test('should preserve the sign of `0`', function(assert) {
    assert.expect(1);

    var array = [-0, 1],
        largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubOne),
        others = [[1], largeArray],
        expected = lodashStable.map(others, lodashStable.constant(['-0']));

    var actual = lodashStable.map(others, function(other) {
      return lodashStable.map(_.differenceWith(array, other, lodashStable.eq), lodashStable.toString);
    });

    assert.deepEqual(actual, expected);
  });
}());