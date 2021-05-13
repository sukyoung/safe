QUnit.module('lodash.negate');

(function() {
  QUnit.test('should create a function that negates the result of `func`', function(assert) {
    assert.expect(2);

    var negate = _.negate(isEven);

    assert.strictEqual(negate(1), true);
    assert.strictEqual(negate(2), false);
  });

  QUnit.test('should create a function that negates the result of `func`', function(assert) {
    assert.expect(2);

    var negate = _.negate(isEven);

    assert.strictEqual(negate(1), true);
    assert.strictEqual(negate(2), false);
  });

  QUnit.test('should create a function that accepts multiple arguments', function(assert) {
    assert.expect(1);

    var argCount,
        count = 5,
        negate = _.negate(function() { argCount = arguments.length; }),
        expected = lodashStable.times(count, stubTrue);

    var actual = lodashStable.times(count, function(index) {
      switch (index) {
        case 0: negate(); break;
        case 1: negate(1); break;
        case 2: negate(1, 2); break;
        case 3: negate(1, 2, 3); break;
        case 4: negate(1, 2, 3, 4);
      }
      return argCount == index;
    });

    assert.deepEqual(actual, expected);
  });
}());