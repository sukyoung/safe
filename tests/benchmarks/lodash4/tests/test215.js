QUnit.module('lodash.sampleSize');

(function() {
  var array = [1, 2, 3];

  QUnit.test('should return an array of random elements', function(assert) {
    assert.expect(2);

    var actual = _.sampleSize(array, 2);

    assert.strictEqual(actual.length, 2);
    assert.deepEqual(lodashStable.difference(actual, array), []);
  });

  QUnit.test('should contain elements of the collection', function(assert) {
    assert.expect(1);

    var actual = _.sampleSize(array, array.length).sort();

    assert.deepEqual(actual, array);
  });

  QUnit.test('should treat falsey `size` values, except `undefined`, as `0`', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, function(value) {
      return value === undefined ? ['a'] : [];
    });

    var actual = lodashStable.map(falsey, function(size, index) {
      return index ? _.sampleSize(['a'], size) : _.sampleSize(['a']);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should return an empty array when `n` < `1` or `NaN`', function(assert) {
    assert.expect(3);

    lodashStable.each([0, -1, -Infinity], function(n) {
      assert.deepEqual(_.sampleSize(array, n), []);
    });
  });

  QUnit.test('should return all elements when `n` >= `length`', function(assert) {
    assert.expect(4);

    lodashStable.each([3, 4, Math.pow(2, 32), Infinity], function(n) {
      var actual = _.sampleSize(array, n).sort();
      assert.deepEqual(actual, array);
    });
  });

  QUnit.test('should coerce `n` to an integer', function(assert) {
    assert.expect(1);

    var actual = _.sampleSize(array, 1.6);
    assert.strictEqual(actual.length, 1);
  });

  QUnit.test('should return an empty array for empty collections', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(empties, stubArray);

    var actual = lodashStable.transform(empties, function(result, value) {
      try {
        result.push(_.sampleSize(value, 1));
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should sample an object', function(assert) {
    assert.expect(2);

    var object = { 'a': 1, 'b': 2, 'c': 3 },
        actual = _.sampleSize(object, 2);

    assert.strictEqual(actual.length, 2);
    assert.deepEqual(lodashStable.difference(actual, lodashStable.values(object)), []);
  });

  QUnit.test('should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map([['a']], _.sampleSize);
    assert.deepEqual(actual, [['a']]);
  });
}());