QUnit.module('sortedIndexBy methods');

lodashStable.each(['sortedIndexBy', 'sortedLastIndexBy'], function(methodName) {
  var func = _[methodName],
      isSortedIndexBy = methodName == 'sortedIndexBy';

  QUnit.test('`_.' + methodName + '` should provide correct `iteratee` arguments', function(assert) {
    assert.expect(1);

    var args;

    func([30, 50], 40, function(assert) {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, [40]);
  });

  QUnit.test('`_.' + methodName + '` should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    var objects = [{ 'x': 30 }, { 'x': 50 }],
        actual = func(objects, { 'x': 40 }, 'x');

    assert.strictEqual(actual, 1);
  });

  QUnit.test('`_.' + methodName + '` should avoid calling iteratee when length is 0', function(assert) {
    var objects = [],
        iteratee = function() {
          throw new Error;
        },
        actual = func(objects, { 'x': 50 }, iteratee);

    assert.strictEqual(actual, 0);
  });

  QUnit.test('`_.' + methodName + '` should support arrays larger than `MAX_ARRAY_LENGTH / 2`', function(assert) {
    assert.expect(12);

    lodashStable.each([Math.ceil(MAX_ARRAY_LENGTH / 2), MAX_ARRAY_LENGTH], function(length) {
      var array = [],
          values = [MAX_ARRAY_LENGTH, NaN, undefined];

      array.length = length;

      lodashStable.each(values, function(value) {
        var steps = 0;

        var actual = func(array, value, function(value) {
          steps++;
          return value;
        });

        var expected = (isSortedIndexBy ? !lodashStable.isNaN(value) : lodashStable.isFinite(value))
          ? 0
          : Math.min(length, MAX_ARRAY_INDEX);

        assert.ok(steps == 32 || steps == 33);
        assert.strictEqual(actual, expected);
      });
    });
  });
});