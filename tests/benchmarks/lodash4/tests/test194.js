QUnit.module('lodash.pullAllWith');

(function() {
  QUnit.test('should work with a `comparator`', function(assert) {
    assert.expect(1);

    var objects = [{ 'x': 1, 'y': 1 }, { 'x': 2, 'y': 2 }, { 'x': 3, 'y': 3 }],
        expected = [objects[0], objects[2]],
        actual = _.pullAllWith(objects, [{ 'x': 2, 'y': 2 }], lodashStable.isEqual);

    assert.deepEqual(actual, expected);
  });
}());