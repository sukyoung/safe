QUnit.module('exit early');

lodashStable.each(['_baseEach', 'forEach', 'forEachRight', 'forIn', 'forInRight', 'forOwn', 'forOwnRight', 'transform'], function(methodName) {
  var func = _[methodName];

  QUnit.test('`_.' + methodName + '` can exit early when iterating arrays', function(assert) {
    assert.expect(1);

    if (func) {
      var array = [1, 2, 3],
          values = [];

      func(array, function(value, other) {
        values.push(lodashStable.isArray(value) ? other : value);
        return false;
      });

      assert.deepEqual(values, [lodashStable.endsWith(methodName, 'Right') ? 3 : 1]);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` can exit early when iterating objects', function(assert) {
    assert.expect(1);

    if (func) {
      var object = { 'a': 1, 'b': 2, 'c': 3 },
          values = [];

      func(object, function(value, other) {
        values.push(lodashStable.isArray(value) ? other : value);
        return false;
      });

      assert.strictEqual(values.length, 1);
    }
    else {
      skipAssert(assert);
    }
  });
});