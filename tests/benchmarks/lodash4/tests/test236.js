QUnit.module('lodash.sumBy');

(function() {
  var array = [6, 4, 2],
      objects = [{ 'a': 2 }, { 'a': 3 }, { 'a': 1 }];

  QUnit.test('should work with an `iteratee`', function(assert) {
    assert.expect(1);

    var actual = _.sumBy(objects, function(object) {
      return object.a;
    });

    assert.deepEqual(actual, 6);
  });

  QUnit.test('should provide correct `iteratee` arguments', function(assert) {
    assert.expect(1);

    var args;

    _.sumBy(array, function() {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, [6]);
  });

  QUnit.test('should work with `_.property` shorthands', function(assert) {
    assert.expect(2);

    var arrays = [[2], [3], [1]];
    assert.strictEqual(_.sumBy(arrays, 0), 6);
    assert.strictEqual(_.sumBy(objects, 'a'), 6);
  });
}());