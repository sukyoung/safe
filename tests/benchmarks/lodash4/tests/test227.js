QUnit.module('lodash.sortedUniq');

(function() {
  QUnit.test('should return unique values of a sorted array', function(assert) {
    assert.expect(3);

    var expected = [1, 2, 3];

    lodashStable.each([[1, 2, 3], [1, 1, 2, 2, 3], [1, 2, 3, 3, 3, 3, 3]], function(array) {
      assert.deepEqual(_.sortedUniq(array), expected);
    });
  });
}());