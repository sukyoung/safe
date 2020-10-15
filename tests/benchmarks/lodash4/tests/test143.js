QUnit.module('indexOf methods');

lodashStable.each(['indexOf', 'lastIndexOf', 'sortedIndexOf', 'sortedLastIndexOf'], function(methodName) {
  var func = _[methodName],
      isIndexOf = !/last/i.test(methodName),
      isSorted = /^sorted/.test(methodName);

  QUnit.test('`_.' + methodName + '` should accept a falsey `array`', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, lodashStable.constant(-1));

    var actual = lodashStable.map(falsey, function(array, index) {
      try {
        return index ? func(array) : func();
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should return `-1` for an unmatched value', function(assert) {
    assert.expect(5);

    var array = [1, 2, 3],
        empty = [];

    assert.strictEqual(func(array, 4), -1);
    assert.strictEqual(func(array, 4, true), -1);
    assert.strictEqual(func(array, undefined, true), -1);

    assert.strictEqual(func(empty, undefined), -1);
    assert.strictEqual(func(empty, undefined, true), -1);
  });

  QUnit.test('`_.' + methodName + '` should not match values on empty arrays', function(assert) {
    assert.expect(2);

    var array = [];
    array[-1] = 0;

    assert.strictEqual(func(array, undefined), -1);
    assert.strictEqual(func(array, 0, true), -1);
  });

  QUnit.test('`_.' + methodName + '` should match `NaN`', function(assert) {
    assert.expect(3);

    var array = isSorted
      ? [1, 2, NaN, NaN]
      : [1, NaN, 3, NaN, 5, NaN];

    if (isSorted) {
      assert.strictEqual(func(array, NaN, true), isIndexOf ? 2 : 3);
      skipAssert(assert, 2);
    }
    else {
      assert.strictEqual(func(array, NaN), isIndexOf ? 1 : 5);
      assert.strictEqual(func(array, NaN, 2), isIndexOf ? 3 : 1);
      assert.strictEqual(func(array, NaN, -2), isIndexOf ? 5 : 3);
    }
  });

  QUnit.test('`_.' + methodName + '` should match `-0` as `0`', function(assert) {
    assert.expect(2);

    assert.strictEqual(func([-0], 0), 0);
    assert.strictEqual(func([0], -0), 0);
  });
});