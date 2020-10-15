QUnit.module('isIterateeCall');

(function() {
  var array = [1],
      func = _._isIterateeCall,
      object =  { 'a': 1 };

  QUnit.test('should return `true` for iteratee calls', function(assert) {
    assert.expect(3);

    function Foo() {}
    Foo.prototype.a = 1;

    if (func) {
      assert.strictEqual(func(1, 0, array), true);
      assert.strictEqual(func(1, 'a', object), true);
      assert.strictEqual(func(1, 'a', new Foo), true);
    }
    else {
      skipAssert(assert, 3);
    }
  });

  QUnit.test('should return `false` for non-iteratee calls', function(assert) {
    assert.expect(4);

    if (func) {
      assert.strictEqual(func(2, 0, array), false);
      assert.strictEqual(func(1, 1.1, array), false);
      assert.strictEqual(func(1, 0, { 'length': MAX_SAFE_INTEGER + 1 }), false);
      assert.strictEqual(func(1, 'b', object), false);
    }
    else {
      skipAssert(assert, 4);
    }
  });

  QUnit.test('should work with `NaN` values', function(assert) {
    assert.expect(2);

    if (func) {
      assert.strictEqual(func(NaN, 0, [NaN]), true);
      assert.strictEqual(func(NaN, 'a', { 'a': NaN }), true);
    }
    else {
      skipAssert(assert, 2);
    }
  });

  QUnit.test('should not error when `index` is an object without a `toString` method', function(assert) {
    assert.expect(1);

    if (func) {
      try {
        var actual = func(1, { 'toString': null }, [1]);
      } catch (e) {
        var message = e.message;
      }
      assert.strictEqual(actual, false, message || '');
    }
    else {
      skipAssert(assert);
    }
  });
}());