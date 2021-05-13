QUnit.module('uniqBy methods');

lodashStable.each(['uniqBy', 'sortedUniqBy'], function(methodName) {
  var func = _[methodName],
      isSorted = methodName == 'sortedUniqBy',
      objects = [{ 'a': 2 }, { 'a': 3 }, { 'a': 1 }, { 'a': 2 }, { 'a': 3 }, { 'a': 1 }];

  if (isSorted) {
    objects = _.sortBy(objects, 'a');
  }
  QUnit.test('`_.' + methodName + '` should work with an `iteratee`', function(assert) {
    assert.expect(1);

    var expected = isSorted ? [{ 'a': 1 }, { 'a': 2 }, { 'a': 3 }] : objects.slice(0, 3);

    var actual = func(objects, function(object) {
      return object.a;
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should work with large arrays', function(assert) {
    assert.expect(2);

    var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function() {
      return [1, 2];
    });

    var actual = func(largeArray, String);
    assert.strictEqual(actual[0], largeArray[0]);
    assert.deepEqual(actual, [[1, 2]]);
  });

  QUnit.test('`_.' + methodName + '` should provide correct `iteratee` arguments', function(assert) {
    assert.expect(1);

    var args;

    func(objects, function() {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, [objects[0]]);
  });

  QUnit.test('`_.' + methodName + '` should work with `_.property` shorthands', function(assert) {
    assert.expect(2);

    var expected = isSorted ? [{ 'a': 1 }, { 'a': 2 }, { 'a': 3 }] : objects.slice(0, 3),
        actual = func(objects, 'a');

    assert.deepEqual(actual, expected);

    var arrays = [[2], [3], [1], [2], [3], [1]];
    if (isSorted) {
      arrays = lodashStable.sortBy(arrays, 0);
    }
    expected = isSorted ? [[1], [2], [3]] : arrays.slice(0, 3);
    actual = func(arrays, 0);

    assert.deepEqual(actual, expected);
  });

  lodashStable.each({
    'an array': [0, 'a'],
    'an object': { '0': 'a' },
    'a number': 0,
    'a string': '0'
  },
  function(iteratee, key) {
    QUnit.test('`_.' + methodName + '` should work with ' + key + ' for `iteratee`', function(assert) {
      assert.expect(1);

      var actual = func([['a'], ['a'], ['b']], iteratee);
      assert.deepEqual(actual, [['a'], ['b']]);
    });
  });
});