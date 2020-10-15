QUnit.module('range methods');

lodashStable.each(['range', 'rangeRight'], function(methodName) {
  var func = _[methodName],
      isRange = methodName == 'range';

  function resolve(range) {
    return isRange ? range : range.reverse();
  }

  QUnit.test('`_.' + methodName + '` should infer the sign of `step` when only `end` is given', function(assert) {
    assert.expect(2);

    assert.deepEqual(func(4), resolve([0, 1, 2, 3]));
    assert.deepEqual(func(-4), resolve([0, -1, -2, -3]));
  });

  QUnit.test('`_.' + methodName + '` should infer the sign of `step` when only `start` and `end` are given', function(assert) {
    assert.expect(2);

    assert.deepEqual(func(1, 5), resolve([1, 2, 3, 4]));
    assert.deepEqual(func(5, 1), resolve([5, 4, 3, 2]));
  });

  QUnit.test('`_.' + methodName + '` should work with a `start`, `end`, and `step`', function(assert) {
    assert.expect(3);

    assert.deepEqual(func(0, -4, -1), resolve([0, -1, -2, -3]));
    assert.deepEqual(func(5, 1, -1), resolve([5, 4, 3, 2]));
    assert.deepEqual(func(0, 20, 5), resolve([0, 5, 10, 15]));
  });

  QUnit.test('`_.' + methodName + '` should support a `step` of `0`', function(assert) {
    assert.expect(1);

    assert.deepEqual(func(1, 4, 0), [1, 1, 1]);
  });

  QUnit.test('`_.' + methodName + '` should work with a `step` larger than `end`', function(assert) {
    assert.expect(1);

    assert.deepEqual(func(1, 5, 20), [1]);
  });

  QUnit.test('`_.' + methodName + '` should work with a negative `step`', function(assert) {
    assert.expect(2);

    assert.deepEqual(func(0, -4, -1), resolve([0, -1, -2, -3]));
    assert.deepEqual(func(21, 10, -3), resolve([21, 18, 15, 12]));
  });

  QUnit.test('`_.' + methodName + '` should support `start` of `-0`', function(assert) {
    assert.expect(1);

    var actual = func(-0, 1);
    assert.strictEqual(1 / actual[0], -Infinity);
  });

  QUnit.test('`_.' + methodName + '` should treat falsey `start` as `0`', function(assert) {
    assert.expect(13);

    lodashStable.each(falsey, function(value, index) {
      if (index) {
        assert.deepEqual(func(value), []);
        assert.deepEqual(func(value, 1), [0]);
      } else {
        assert.deepEqual(func(), []);
      }
    });
  });

  QUnit.test('`_.' + methodName + '` should coerce arguments to finite numbers', function(assert) {
    assert.expect(1);

    var actual = [
      func('1'),
      func('0', 1),
      func(0, 1, '1'),
      func(NaN),
      func(NaN, NaN)
    ];

    assert.deepEqual(actual, [[0], [0], [0], [], []]);
  });

  QUnit.test('`_.' + methodName + '` should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(2);

    var array = [1, 2, 3],
        object = { 'a': 1, 'b': 2, 'c': 3 },
        expected = lodashStable.map([[0], [0, 1], [0, 1, 2]], resolve);

    lodashStable.each([array, object], function(collection) {
      var actual = lodashStable.map(collection, func);
      assert.deepEqual(actual, expected);
    });
  });
});