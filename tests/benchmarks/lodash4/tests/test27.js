QUnit.module('lodash.concat');

(function() {
  QUnit.test('should shallow clone `array`', function(assert) {
    assert.expect(2);

    var array = [1, 2, 3],
        actual = _.concat(array);

    assert.deepEqual(actual, array);
    assert.notStrictEqual(actual, array);
  });

  QUnit.test('should concat arrays and values', function(assert) {
    assert.expect(2);

    var array = [1],
        actual = _.concat(array, 2, [3], [[4]]);

    assert.deepEqual(actual, [1, 2, 3, [4]]);
    assert.deepEqual(array, [1]);
  });

  QUnit.test('should cast non-array `array` values to arrays', function(assert) {
    assert.expect(2);

    var values = [, null, undefined, false, true, 1, NaN, 'a'];

    var expected = lodashStable.map(values, function(value, index) {
      return index ? [value] : [];
    });

    var actual = lodashStable.map(values, function(value, index) {
      return index ? _.concat(value) : _.concat();
    });

    assert.deepEqual(actual, expected);

    expected = lodashStable.map(values, function(value) {
      return [value, 2, [3]];
    });

    actual = lodashStable.map(values, function(value) {
      return _.concat(value, [2], [[3]]);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should treat sparse arrays as dense', function(assert) {
    assert.expect(3);

    var expected = [],
        actual = _.concat(Array(1), Array(1));

    expected.push(undefined, undefined);

    assert.ok('0'in actual);
    assert.ok('1' in actual);
    assert.deepEqual(actual, expected);
  });

  QUnit.test('should return a new wrapped array', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var array = [1],
          wrapped = _(array).concat([2, 3]),
          actual = wrapped.value();

      assert.deepEqual(array, [1]);
      assert.deepEqual(actual, [1, 2, 3]);
    }
    else {
      skipAssert(assert, 2);
    }
  });
}());