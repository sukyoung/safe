QUnit.module('omit methods');

lodashStable.each(['omit', 'omitBy'], function(methodName) {
  var expected = { 'b': 2, 'd': 4 },
      func = _[methodName],
      object = { 'a': 1, 'b': 2, 'c': 3, 'd': 4 },
      resolve = lodashStable.nthArg(1);

  if (methodName == 'omitBy') {
    resolve = function(object, props) {
      props = lodashStable.castArray(props);
      return function(value) {
        return lodashStable.some(props, function(key) {
          key = lodashStable.isSymbol(key) ? key : lodashStable.toString(key);
          return object[key] === value;
        });
      };
    };
  }
  QUnit.test('`_.' + methodName + '` should create an object with omitted string keyed properties', function(assert) {
    assert.expect(2);

    assert.deepEqual(func(object, resolve(object, 'a')), { 'b': 2, 'c': 3, 'd': 4 });
    assert.deepEqual(func(object, resolve(object, ['a', 'c'])), expected);
  });

  QUnit.test('`_.' + methodName + '` should include inherited string keyed properties', function(assert) {
    assert.expect(1);

    function Foo() {}
    Foo.prototype = object;

    assert.deepEqual(func(new Foo, resolve(object, ['a', 'c'])), expected);
  });

  QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function(assert) {
    assert.expect(1);

    var object = { '-0': 'a', '0': 'b' },
        props = [-0, Object(-0), 0, Object(0)],
        expected = [{ '0': 'b' }, { '0': 'b' }, { '-0': 'a' }, { '-0': 'a' }];

    var actual = lodashStable.map(props, function(key) {
      return func(object, resolve(object, key));
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should include symbols', function(assert) {
    assert.expect(3);

    function Foo() {
      this.a = 0;
      this[symbol] = 1;
    }

    if (Symbol) {
      var symbol2 = Symbol('b');
      Foo.prototype[symbol2] = 2;

      var symbol3 = Symbol('c');
      defineProperty(Foo.prototype, symbol3, {
        'configurable': true,
        'enumerable': false,
        'writable': true,
        'value': 3
      });

      var foo = new Foo,
          actual = func(foo, resolve(foo, 'a'));

      assert.strictEqual(actual[symbol], 1);
      assert.strictEqual(actual[symbol2], 2);
      assert.notOk(symbol3 in actual);
    }
    else {
      skipAssert(assert, 3);
    }
  });

  QUnit.test('`_.' + methodName + '` should create an object with omitted symbols', function(assert) {
    assert.expect(8);

    function Foo() {
      this.a = 0;
      this[symbol] = 1;
    }

    if (Symbol) {
      var symbol2 = Symbol('b');
      Foo.prototype[symbol2] = 2;

      var symbol3 = Symbol('c');
      defineProperty(Foo.prototype, symbol3, {
        'configurable': true,
        'enumerable': false,
        'writable': true,
        'value': 3
      });

      var foo = new Foo,
          actual = func(foo, resolve(foo, symbol));

      assert.strictEqual(actual.a, 0);
      assert.notOk(symbol in actual);
      assert.strictEqual(actual[symbol2], 2);
      assert.notOk(symbol3 in actual);

      actual = func(foo, resolve(foo, symbol2));

      assert.strictEqual(actual.a, 0);
      assert.strictEqual(actual[symbol], 1);
      assert.notOk(symbol2 in actual);
      assert.notOk(symbol3 in actual);
    }
    else {
      skipAssert(assert, 8);
    }
  });

  QUnit.test('`_.' + methodName + '` should work with an array `object`', function(assert) {
    assert.expect(1);

    var array = [1, 2, 3];
    assert.deepEqual(func(array, resolve(array, ['0', '2'])), { '1': 2 });
  });
});