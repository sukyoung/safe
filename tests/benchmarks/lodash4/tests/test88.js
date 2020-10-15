QUnit.module('lodash.initial');

(function() {
  var array = [1, 2, 3];

  QUnit.test('should accept a falsey `array`', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, stubArray);

    var actual = lodashStable.map(falsey, function(array, index) {
      try {
        return index ? _.initial(array) : _.initial();
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should exclude last element', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.initial(array), [1, 2]);
  });

  QUnit.test('should return an empty when querying empty arrays', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.initial([]), []);
  });

  QUnit.test('should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(1);

    var array = [[1, 2, 3], [4, 5, 6], [7, 8, 9]],
        actual = lodashStable.map(array, _.initial);

    assert.deepEqual(actual, [[1, 2], [4, 5], [7, 8]]);
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(4);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE),
          values = [];

      var actual = _(array).initial().filter(function(value) {
        values.push(value);
        return false;
      })
      .value();

      assert.deepEqual(actual, []);
      assert.deepEqual(values, _.initial(array));

      values = [];

      actual = _(array).filter(function(value) {
        values.push(value);
        return isEven(value);
      })
      .initial()
      .value();

      assert.deepEqual(actual, _.initial(lodashStable.filter(array, isEven)));
      assert.deepEqual(values, array);
    }
    else {
      skipAssert(assert, 4);
    }
  });
}());