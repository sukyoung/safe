QUnit.module('lodash.debounce');

(function() {
  QUnit.test('should debounce a function', function(assert) {
    assert.expect(6);

    var done = assert.async();

    var callCount = 0;

    var debounced = _.debounce(function(value) {
      ++callCount;
      return value;
    }, 32);

    var results = [debounced('a'), debounced('b'), debounced('c')];
    assert.deepEqual(results, [undefined, undefined, undefined]);
    assert.strictEqual(callCount, 0);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);

      var results = [debounced('d'), debounced('e'), debounced('f')];
      assert.deepEqual(results, ['c', 'c', 'c']);
      assert.strictEqual(callCount, 1);
    }, 128);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 256);
  });

  QUnit.test('subsequent debounced calls return the last `func` result', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var debounced = _.debounce(identity, 32);
    debounced('a');

    setTimeout(function() {
      assert.notEqual(debounced('b'), 'b');
    }, 64);

    setTimeout(function() {
      assert.notEqual(debounced('c'), 'c');
      done();
    }, 128);
  });

  QUnit.test('should not immediately call `func` when `wait` is `0`', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0,
        debounced = _.debounce(function() { ++callCount; }, 0);

    debounced();
    debounced();
    assert.strictEqual(callCount, 0);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
      done();
    }, 5);
  });

  QUnit.test('should apply default options', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0,
        debounced = _.debounce(function() { callCount++; }, 32, {});

    debounced();
    assert.strictEqual(callCount, 0);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
      done();
    }, 64);
  });

  QUnit.test('should support a `leading` option', function(assert) {
    assert.expect(4);

    var done = assert.async();

    var callCounts = [0, 0];

    var withLeading = _.debounce(function() {
      callCounts[0]++;
    }, 32, { 'leading': true });

    var withLeadingAndTrailing = _.debounce(function() {
      callCounts[1]++;
    }, 32, { 'leading': true });

    withLeading();
    assert.strictEqual(callCounts[0], 1);

    withLeadingAndTrailing();
    withLeadingAndTrailing();
    assert.strictEqual(callCounts[1], 1);

    setTimeout(function() {
      assert.deepEqual(callCounts, [1, 2]);

      withLeading();
      assert.strictEqual(callCounts[0], 2);

      done();
    }, 64);
  });

  QUnit.test('subsequent leading debounced calls return the last `func` result', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var debounced = _.debounce(identity, 32, { 'leading': true, 'trailing': false }),
        results = [debounced('a'), debounced('b')];

    assert.deepEqual(results, ['a', 'a']);

    setTimeout(function() {
      var results = [debounced('c'), debounced('d')];
      assert.deepEqual(results, ['c', 'c']);
      done();
    }, 64);
  });

  QUnit.test('should support a `trailing` option', function(assert) {
    assert.expect(4);

    var done = assert.async();

    var withCount = 0,
        withoutCount = 0;

    var withTrailing = _.debounce(function() {
      withCount++;
    }, 32, { 'trailing': true });

    var withoutTrailing = _.debounce(function() {
      withoutCount++;
    }, 32, { 'trailing': false });

    withTrailing();
    assert.strictEqual(withCount, 0);

    withoutTrailing();
    assert.strictEqual(withoutCount, 0);

    setTimeout(function() {
      assert.strictEqual(withCount, 1);
      assert.strictEqual(withoutCount, 0);
      done();
    }, 64);
  });

  QUnit.test('should support a `maxWait` option', function(assert) {
    assert.expect(4);

    var done = assert.async();

    var callCount = 0;

    var debounced = _.debounce(function(value) {
      ++callCount;
      return value;
    }, 32, { 'maxWait': 64 });

    debounced();
    debounced();
    assert.strictEqual(callCount, 0);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
      debounced();
      debounced();
      assert.strictEqual(callCount, 1);
    }, 128);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 256);
  });

  QUnit.test('should support `maxWait` in a tight loop', function(assert) {
    assert.expect(1);

    var done = assert.async();

    var limit = (argv || isPhantom) ? 1000 : 320,
        withCount = 0,
        withoutCount = 0;

    var withMaxWait = _.debounce(function() {
      withCount++;
    }, 64, { 'maxWait': 128 });

    var withoutMaxWait = _.debounce(function() {
      withoutCount++;
    }, 96);

    var start = +new Date;
    while ((new Date - start) < limit) {
      withMaxWait();
      withoutMaxWait();
    }
    var actual = [Boolean(withoutCount), Boolean(withCount)];
    setTimeout(function() {
      assert.deepEqual(actual, [false, true]);
      done();
    }, 1);
  });

  QUnit.test('should queue a trailing call for subsequent debounced calls after `maxWait`', function(assert) {
    assert.expect(1);

    var done = assert.async();

    var callCount = 0;

    var debounced = _.debounce(function() {
      ++callCount;
    }, 200, { 'maxWait': 200 });

    debounced();

    setTimeout(debounced, 190);
    setTimeout(debounced, 200);
    setTimeout(debounced, 210);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 500);
  });

  QUnit.test('should cancel `maxDelayed` when `delayed` is invoked', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0;

    var debounced = _.debounce(function() {
      callCount++;
    }, 32, { 'maxWait': 64 });

    debounced();

    setTimeout(function() {
      debounced();
      assert.strictEqual(callCount, 1);
    }, 128);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 192);
  });

  QUnit.test('should invoke the trailing call with the correct arguments and `this` binding', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var actual,
        callCount = 0,
        object = {};

    var debounced = _.debounce(function(value) {
      actual = [this];
      push.apply(actual, arguments);
      return ++callCount != 2;
    }, 32, { 'leading': true, 'maxWait': 64 });

    while (true) {
      if (!debounced.call(object, 'a')) {
        break;
      }
    }
    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      assert.deepEqual(actual, [object, 'a']);
      done();
    }, 64);
  });
}());