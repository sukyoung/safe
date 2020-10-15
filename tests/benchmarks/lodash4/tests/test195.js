QUnit.module('pull methods');

lodashStable.each(['pull', 'pullAll', 'pullAllWith'], function(methodName) {
  var func = _[methodName],
      isPull = methodName == 'pull';

  function pull(array, values) {
    return isPull
      ? func.apply(undefined, [array].concat(values))
      : func(array, values);
  }

  QUnit.test('`_.' + methodName + '` should modify and return the array', function(assert) {
    assert.expect(2);

    var array = [1, 2, 3],
        actual = pull(array, [1, 3]);

    assert.strictEqual(actual, array);
    assert.deepEqual(array, [2]);
  });

  QUnit.test('`_.' + methodName + '` should preserve holes in arrays', function(assert) {
    assert.expect(2);

    var array = [1, 2, 3, 4];
    delete array[1];
    delete array[3];

    pull(array, [1]);
    assert.notOk('0' in array);
    assert.notOk('2' in array);
  });

  QUnit.test('`_.' + methodName + '` should treat holes as `undefined`', function(assert) {
    assert.expect(1);

    var array = [1, 2, 3];
    delete array[1];

    pull(array, [undefined]);
    assert.deepEqual(array, [1, 3]);
  });

  QUnit.test('`_.' + methodName + '` should match `NaN`', function(assert) {
    assert.expect(1);

    var array = [1, NaN, 3, NaN];

    pull(array, [NaN]);
    assert.deepEqual(array, [1, 3]);
  });
});