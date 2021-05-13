QUnit.module('intersection methods');

lodashStable.each(['intersection', 'intersectionBy', 'intersectionWith'], function(methodName) {
  var func = _[methodName];

  QUnit.test('`_.' + methodName + '` should return the intersection of two arrays', function(assert) {
    assert.expect(1);

    var actual = func([2, 1], [2, 3]);
    assert.deepEqual(actual, [2]);
  });

  QUnit.test('`_.' + methodName + '` should return the intersection of multiple arrays', function(assert) {
    assert.expect(1);

    var actual = func([2, 1, 2, 3], [3, 4], [3, 2]);
    assert.deepEqual(actual, [3]);
  });

  QUnit.test('`_.' + methodName + '` should return an array of unique values', function(assert) {
    assert.expect(1);

    var actual = func([1, 1, 3, 2, 2], [5, 2, 2, 1, 4], [2, 1, 1]);
    assert.deepEqual(actual, [1, 2]);
  });

  QUnit.test('`_.' + methodName + '` should work with a single array', function(assert) {
    assert.expect(1);

    var actual = func([1, 1, 3, 2, 2]);
    assert.deepEqual(actual, [1, 3, 2]);
  });

  QUnit.test('`_.' + methodName + '` should work with `arguments` objects', function(assert) {
    assert.expect(2);

    var array = [0, 1, null, 3],
        expected = [1, 3];

    assert.deepEqual(func(array, args), expected);
    assert.deepEqual(func(args, array), expected);
  });

  QUnit.test('`_.' + methodName + '` should treat `-0` as `0`', function(assert) {
    assert.expect(1);

    var values = [-0, 0],
        expected = lodashStable.map(values, lodashStable.constant(['0']));

    var actual = lodashStable.map(values, function(value) {
      return lodashStable.map(func(values, [value]), lodashStable.toString);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should match `NaN`', function(assert) {
    assert.expect(1);

    var actual = func([1, NaN, 3], [NaN, 5, NaN]);
    assert.deepEqual(actual, [NaN]);
  });

  QUnit.test('`_.' + methodName + '` should work with large arrays of `-0` as `0`', function(assert) {
    assert.expect(1);

    var values = [-0, 0],
        expected = lodashStable.map(values, lodashStable.constant(['0']));

    var actual = lodashStable.map(values, function(value) {
      var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(value));
      return lodashStable.map(func(values, largeArray), lodashStable.toString);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should work with large arrays of `NaN`', function(assert) {
    assert.expect(1);

    var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubNaN);
    assert.deepEqual(func([1, NaN, 3], largeArray), [NaN]);
  });

  QUnit.test('`_.' + methodName + '` should work with large arrays of objects', function(assert) {
    assert.expect(2);

    var object = {},
        largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(object));

    assert.deepEqual(func([object], largeArray), [object]);
    assert.deepEqual(func(lodashStable.range(LARGE_ARRAY_SIZE), [1]), [1]);
  });

  QUnit.test('`_.' + methodName + '` should treat values that are not arrays or `arguments` objects as empty', function(assert) {
    assert.expect(3);

    var array = [0, 1, null, 3];
    assert.deepEqual(func(array, 3, { '0': 1 }, null), []);
    assert.deepEqual(func(null, array, null, [2, 3]), []);
    assert.deepEqual(func(array, null, args, null), []);
  });

  QUnit.test('`_.' + methodName + '` should return a wrapped value when chaining', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var wrapped = _([1, 3, 2])[methodName]([5, 2, 1, 4]);
      assert.ok(wrapped instanceof _);
      assert.deepEqual(wrapped.value(), [1, 2]);
    }
    else {
      skipAssert(assert, 2);
    }
  });
});