QUnit.module('lodash.nthArg');

(function() {
  var args = ['a', 'b', 'c', 'd'];

  QUnit.test('should create a function that returns its nth argument', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map(args, function(value, index) {
      var func = _.nthArg(index);
      return func.apply(undefined, args);
    });

    assert.deepEqual(actual, args);
  });

  QUnit.test('should work with a negative `n`', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map(lodashStable.range(1, args.length + 1), function(n) {
      var func = _.nthArg(-n);
      return func.apply(undefined, args);
    });

    assert.deepEqual(actual, ['d', 'c', 'b', 'a']);
  });

  QUnit.test('should coerce `n` to an integer', function(assert) {
    assert.expect(2);

    var values = falsey,
        expected = lodashStable.map(values, stubA);

    var actual = lodashStable.map(values, function(n) {
      var func = n ? _.nthArg(n) : _.nthArg();
      return func.apply(undefined, args);
    });

    assert.deepEqual(actual, expected);

    values = ['1', 1.6];
    expected = lodashStable.map(values, stubB);

    actual = lodashStable.map(values, function(n) {
      var func = _.nthArg(n);
      return func.apply(undefined, args);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should return `undefined` for empty arrays', function(assert) {
    assert.expect(1);

    var func = _.nthArg(1);
    assert.strictEqual(func(), undefined);
  });

  QUnit.test('should return `undefined` for non-indexes', function(assert) {
    assert.expect(1);

    var values = [Infinity, args.length],
        expected = lodashStable.map(values, noop);

    var actual = lodashStable.map(values, function(n) {
      var func = _.nthArg(n);
      return func.apply(undefined, args);
    });

    assert.deepEqual(actual, expected);
  });
}());