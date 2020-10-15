QUnit.module('lodash.cond');

(function() {
  QUnit.test('should create a conditional function', function(assert) {
    assert.expect(3);

    var cond = _.cond([
      [lodashStable.matches({ 'a': 1 }),     stubA],
      [lodashStable.matchesProperty('b', 1), stubB],
      [lodashStable.property('c'),           stubC]
    ]);

    assert.strictEqual(cond({ 'a':  1, 'b': 2, 'c': 3 }), 'a');
    assert.strictEqual(cond({ 'a':  0, 'b': 1, 'c': 2 }), 'b');
    assert.strictEqual(cond({ 'a': -1, 'b': 0, 'c': 1 }), 'c');
  });

  QUnit.test('should provide arguments to functions', function(assert) {
    assert.expect(2);

    var args1,
        args2,
        expected = ['a', 'b', 'c'];

    var cond = _.cond([[
      function() { args1 || (args1 = slice.call(arguments)); return true; },
      function() { args2 || (args2 = slice.call(arguments)); }
    ]]);

    cond('a', 'b', 'c');

    assert.deepEqual(args1, expected);
    assert.deepEqual(args2, expected);
  });

  QUnit.test('should work with predicate shorthands', function(assert) {
    assert.expect(3);

    var cond = _.cond([
      [{ 'a': 1 }, stubA],
      [['b', 1],   stubB],
      ['c',        stubC]
    ]);

    assert.strictEqual(cond({ 'a':  1, 'b': 2, 'c': 3 }), 'a');
    assert.strictEqual(cond({ 'a':  0, 'b': 1, 'c': 2 }), 'b');
    assert.strictEqual(cond({ 'a': -1, 'b': 0, 'c': 1 }), 'c');
  });

  QUnit.test('should return `undefined` when no condition is met', function(assert) {
    assert.expect(1);

    var cond = _.cond([[stubFalse, stubA]]);
    assert.strictEqual(cond({ 'a': 1 }), undefined);
  });

  QUnit.test('should throw a TypeError if `pairs` is not composed of functions', function(assert) {
    assert.expect(2);

    lodashStable.each([false, true], function(value) {
      assert.raises(function() { _.cond([[stubTrue, value]])(); }, TypeError);
    });
  });

  QUnit.test('should use `this` binding of function for `pairs`', function(assert) {
    assert.expect(1);

    var cond = _.cond([
      [function(a) { return this[a]; }, function(a, b) { return this[b]; }]
    ]);

    var object = { 'cond': cond, 'a': 1, 'b': 2 };
    assert.strictEqual(object.cond('a', 'b'), 2);
  });
}());