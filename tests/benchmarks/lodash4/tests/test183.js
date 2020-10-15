QUnit.module('partial methods');

lodashStable.each(['partial', 'partialRight'], function(methodName) {
  var func = _[methodName],
      isPartial = methodName == 'partial',
      ph = func.placeholder;

  QUnit.test('`_.' + methodName + '` partially applies arguments', function(assert) {
    assert.expect(1);

    var par = func(identity, 'a');
    assert.strictEqual(par(), 'a');
  });

  QUnit.test('`_.' + methodName + '` creates a function that can be invoked with additional arguments', function(assert) {
    assert.expect(1);

    var fn = function(a, b) { return [a, b]; },
        par = func(fn, 'a'),
        expected = isPartial ? ['a', 'b'] : ['b', 'a'];

    assert.deepEqual(par('b'), expected);
  });

  QUnit.test('`_.' + methodName + '` works when there are no partially applied arguments and the created function is invoked without additional arguments', function(assert) {
    assert.expect(1);

    var fn = function() { return arguments.length; },
        par = func(fn);

    assert.strictEqual(par(), 0);
  });

  QUnit.test('`_.' + methodName + '` works when there are no partially applied arguments and the created function is invoked with additional arguments', function(assert) {
    assert.expect(1);

    var par = func(identity);
    assert.strictEqual(par('a'), 'a');
  });

  QUnit.test('`_.' + methodName + '` should support placeholders', function(assert) {
    assert.expect(4);

    var fn = function() { return slice.call(arguments); },
        par = func(fn, ph, 'b', ph);

    assert.deepEqual(par('a', 'c'), ['a', 'b', 'c']);
    assert.deepEqual(par('a'), ['a', 'b', undefined]);
    assert.deepEqual(par(), [undefined, 'b', undefined]);

    if (isPartial) {
      assert.deepEqual(par('a', 'c', 'd'), ['a', 'b', 'c', 'd']);
    } else {
      par = func(fn, ph, 'c', ph);
      assert.deepEqual(par('a', 'b', 'd'), ['a', 'b', 'c', 'd']);
    }
  });

  QUnit.test('`_.' + methodName + '` should use `_.placeholder` when set', function(assert) {
    assert.expect(1);

    if (!isModularize) {
      var _ph = _.placeholder = {},
          fn = function() { return slice.call(arguments); },
          par = func(fn, _ph, 'b', ph),
          expected = isPartial ? ['a', 'b', ph, 'c'] : ['a', 'c', 'b', ph];

      assert.deepEqual(par('a', 'c'), expected);
      delete _.placeholder;
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` creates a function with a `length` of `0`', function(assert) {
    assert.expect(1);

    var fn = function(a, b, c) {},
        par = func(fn, 'a');

    assert.strictEqual(par.length, 0);
  });

  QUnit.test('`_.' + methodName + '` should ensure `new par` is an instance of `func`', function(assert) {
    assert.expect(2);

    function Foo(value) {
      return value && object;
    }

    var object = {},
        par = func(Foo);

    assert.ok(new par instanceof Foo);
    assert.strictEqual(new par(true), object);
  });

  QUnit.test('`_.' + methodName + '` should clone metadata for created functions', function(assert) {
    assert.expect(3);

    function greet(greeting, name) {
      return greeting + ' ' + name;
    }

    var par1 = func(greet, 'hi'),
        par2 = func(par1, 'barney'),
        par3 = func(par1, 'pebbles');

    assert.strictEqual(par1('fred'), isPartial ? 'hi fred' : 'fred hi');
    assert.strictEqual(par2(), isPartial ? 'hi barney'  : 'barney hi');
    assert.strictEqual(par3(), isPartial ? 'hi pebbles' : 'pebbles hi');
  });

  QUnit.test('`_.' + methodName + '` should work with curried functions', function(assert) {
    assert.expect(2);

    var fn = function(a, b, c) { return a + b + c; },
        curried = _.curry(func(fn, 1), 2);

    assert.strictEqual(curried(2, 3), 6);
    assert.strictEqual(curried(2)(3), 6);
  });

  QUnit.test('should work with placeholders and curried functions', function(assert) {
    assert.expect(1);

    var fn = function() { return slice.call(arguments); },
        curried = _.curry(fn),
        par = func(curried, ph, 'b', ph, 'd');

    assert.deepEqual(par('a', 'c'), ['a', 'b', 'c', 'd']);
  });
});