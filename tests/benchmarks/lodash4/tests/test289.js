QUnit.module('lodash.zipWith');

(function() {
  QUnit.test('should zip arrays combining grouped elements with `iteratee`', function(assert) {
    assert.expect(2);

    var array1 = [1, 2, 3],
        array2 = [4, 5, 6],
        array3 = [7, 8, 9];

    var actual = _.zipWith(array1, array2, array3, function(a, b, c) {
      return a + b + c;
    });

    assert.deepEqual(actual, [12, 15, 18]);

    var actual = _.zipWith(array1, [], function(a, b) {
      return a + (b || 0);
    });

    assert.deepEqual(actual, [1, 2, 3]);
  });

  QUnit.test('should provide correct `iteratee` arguments', function(assert) {
    assert.expect(1);

    var args;

    _.zipWith([1, 2], [3, 4], [5, 6], function() {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, [1, 3, 5]);
  });

  QUnit.test('should perform a basic zip when `iteratee` is nullish', function(assert) {
    assert.expect(1);

    var array1 = [1, 2],
        array2 = [3, 4],
        values = [, null, undefined],
        expected = lodashStable.map(values, lodashStable.constant(_.zip(array1, array2)));

    var actual = lodashStable.map(values, function(value, index) {
      return index ? _.zipWith(array1, array2, value) : _.zipWith(array1, array2);
    });

    assert.deepEqual(actual, expected);
  });
}());