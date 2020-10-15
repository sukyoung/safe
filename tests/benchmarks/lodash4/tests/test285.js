QUnit.module('xor methods');

lodashStable.each(['xor', 'xorBy', 'xorWith'], function(methodName) {
  var func = _[methodName];

  QUnit.test('`_.' + methodName + '` should return the symmetric difference of two arrays', function(assert) {
    assert.expect(1);

    var actual = func([2, 1], [2, 3]);
    assert.deepEqual(actual, [1, 3]);
  });

  QUnit.test('`_.' + methodName + '` should return the symmetric difference of multiple arrays', function(assert) {
    assert.expect(2);

    var actual = func([2, 1], [2, 3], [3, 4]);
    assert.deepEqual(actual, [1, 4]);

    actual = func([1, 2], [2, 1], [1, 2]);
    assert.deepEqual(actual, []);
  });

  QUnit.test('`_.' + methodName + '` should return an empty array when comparing the same array', function(assert) {
    assert.expect(1);

    var array = [1],
        actual = func(array, array, array);

    assert.deepEqual(actual, []);
  });

  QUnit.test('`_.' + methodName + '` should return an array of unique values', function(assert) {
    assert.expect(2);

    var actual = func([1, 1, 2, 5], [2, 2, 3, 5], [3, 4, 5, 5]);
    assert.deepEqual(actual, [1, 4]);

    actual = func([1, 1]);
    assert.deepEqual(actual, [1]);
  });

  QUnit.test('`_.' + methodName + '` should return a new array when a single array is given', function(assert) {
    assert.expect(1);

    var array = [1];
    assert.notStrictEqual(func(array), array);
  });

  QUnit.test('`_.' + methodName + '` should ignore individual secondary arguments', function(assert) {
    assert.expect(1);

    var array = [0];
    assert.deepEqual(func(array, 3, null, { '0': 1 }), array);
  });

  QUnit.test('`_.' + methodName + '` should ignore values that are not arrays or `arguments` objects', function(assert) {
    assert.expect(3);

    var array = [1, 2];
    assert.deepEqual(func(array, 3, { '0': 1 }, null), array);
    assert.deepEqual(func(null, array, null, [2, 3]), [1, 3]);
    assert.deepEqual(func(array, null, args, null), [3]);
  });

  QUnit.test('`_.' + methodName + '` should return a wrapped value when chaining', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var wrapped = _([1, 2, 3])[methodName]([5, 2, 1, 4]);
      assert.ok(wrapped instanceof _);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` should work when in a lazy sequence before `head` or `last`', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE + 1),
          wrapped = _(array).slice(1)[methodName]([LARGE_ARRAY_SIZE, LARGE_ARRAY_SIZE + 1]);

      var actual = lodashStable.map(['head', 'last'], function(methodName) {
        return wrapped[methodName]();
      });

      assert.deepEqual(actual, [1, LARGE_ARRAY_SIZE + 1]);
    }
    else {
      skipAssert(assert);
    }
  });
});