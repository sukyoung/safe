QUnit.module('lodash.tail');

(function() {
  var array = [1, 2, 3];

  QUnit.test('should accept a falsey `array`', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, stubArray);

    var actual = lodashStable.map(falsey, function(array, index) {
      try {
        return index ? _.tail(array) : _.tail();
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should exclude the first element', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.tail(array), [2, 3]);
  });

  QUnit.test('should return an empty when querying empty arrays', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.tail([]), []);
  });

  QUnit.test('should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(1);

    var array = [[1, 2, 3], [4, 5, 6], [7, 8, 9]],
        actual = lodashStable.map(array, _.tail);

    assert.deepEqual(actual, [[2, 3], [5, 6], [8, 9]]);
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(4);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE),
          values = [];

      var actual = _(array).tail().filter(function(value) {
        values.push(value);
        return false;
      })
      .value();

      assert.deepEqual(actual, []);
      assert.deepEqual(values, array.slice(1));

      values = [];

      actual = _(array).filter(function(value) {
        values.push(value);
        return isEven(value);
      })
      .tail()
      .value();

      assert.deepEqual(actual, _.tail(_.filter(array, isEven)));
      assert.deepEqual(values, array);
    }
    else {
      skipAssert(assert, 4);
    }
  });

  QUnit.test('should not execute subsequent iteratees on an empty array in a lazy sequence', function(assert) {
    assert.expect(4);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE),
          iteratee = function() { pass = false; },
          pass = true,
          actual = _(array).slice(0, 1).tail().map(iteratee).value();

      assert.ok(pass);
      assert.deepEqual(actual, []);

      pass = true;
      actual = _(array).filter().slice(0, 1).tail().map(iteratee).value();

      assert.ok(pass);
      assert.deepEqual(actual, []);
    }
    else {
      skipAssert(assert, 4);
    }
  });
}());