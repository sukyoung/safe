QUnit.module('lodash.endsWith');

(function() {
  var string = 'abc';

  QUnit.test('should return `true` if a string ends with `target`', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.endsWith(string, 'c'), true);
  });

  QUnit.test('should return `false` if a string does not end with `target`', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.endsWith(string, 'b'), false);
  });

  QUnit.test('should work with a `position`', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.endsWith(string, 'b', 2), true);
  });

  QUnit.test('should work with `position` >= `length`', function(assert) {
    assert.expect(4);

    lodashStable.each([3, 5, MAX_SAFE_INTEGER, Infinity], function(position) {
      assert.strictEqual(_.endsWith(string, 'c', position), true);
    });
  });

  QUnit.test('should treat falsey `position` values, except `undefined`, as `0`', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, stubTrue);

    var actual = lodashStable.map(falsey, function(position) {
      return _.endsWith(string, position === undefined ? 'c' : '', position);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should treat a negative `position` as `0`', function(assert) {
    assert.expect(6);

    lodashStable.each([-1, -3, -Infinity], function(position) {
      assert.ok(lodashStable.every(string, function(chr) {
        return !_.endsWith(string, chr, position);
      }));
      assert.strictEqual(_.endsWith(string, '', position), true);
    });
  });

  QUnit.test('should coerce `position` to an integer', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.endsWith(string, 'ab', 2.2), true);
  });
}());