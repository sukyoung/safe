QUnit.module('lodash.throttle');

(function() {
  QUnit.test('should throttle a function', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0,
        throttled = _.throttle(function() { callCount++; }, 32);

    throttled();
    throttled();
    throttled();

    var lastCount = callCount;
    assert.ok(callCount);

    setTimeout(function() {
      assert.ok(callCount > lastCount);
      done();
    }, 64);
  });

  QUnit.test('subsequent calls should return the result of the first call', function(assert) {
    assert.expect(5);

    var done = assert.async();

    var throttled = _.throttle(identity, 32),
        results = [throttled('a'), throttled('b')];

    assert.deepEqual(results, ['a', 'a']);

    setTimeout(function() {
      var results = [throttled('c'), throttled('d')];
      assert.notEqual(results[0], 'a');
      assert.notStrictEqual(results[0], undefined);

      assert.notEqual(results[1], 'd');
      assert.notStrictEqual(results[1], undefined);
      done();
    }, 64);
  });

  QUnit.test('should clear timeout when `func` is called', function(assert) {
    assert.expect(1);

    var done = assert.async();

    if (!isModularize) {
      var callCount = 0,
          dateCount = 0;

      var lodash = _.runInContext({
        'Date': {
          'now': function() {
            return ++dateCount == 5 ? Infinity : +new Date;
          }
        }
      });

      var throttled = lodash.throttle(function() { callCount++; }, 32);

      throttled();
      throttled();

      setTimeout(function() {
        assert.strictEqual(callCount, 2);
        done();
      }, 64);
    }
    else {
      skipAssert(assert);
      done();
    }
  });

  QUnit.test('should not trigger a trailing call when invoked once', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0,
        throttled = _.throttle(function() { callCount++; }, 32);

    throttled();
    assert.strictEqual(callCount, 1);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
      done();
    }, 64);
  });

  lodashStable.times(2, function(index) {
    QUnit.test('should trigger a call when invoked repeatedly' + (index ? ' and `leading` is `false`' : ''), function(assert) {
      assert.expect(1);

      var done = assert.async();

      var callCount = 0,
          limit = (argv || isPhantom) ? 1000 : 320,
          options = index ? { 'leading': false } : {},
          throttled = _.throttle(function() { callCount++; }, 32, options);

      var start = +new Date;
      while ((new Date - start) < limit) {
        throttled();
      }
      var actual = callCount > 1;
      setTimeout(function() {
        assert.ok(actual);
        done();
      }, 1);
    });
  });

  QUnit.test('should trigger a second throttled call as soon as possible', function(assert) {
    assert.expect(3);

    var done = assert.async();

    var callCount = 0;

    var throttled = _.throttle(function() {
      callCount++;
    }, 128, { 'leading': false });

    throttled();

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
      throttled();
    }, 192);

    setTimeout(function() {
      assert.strictEqual(callCount, 1);
    }, 254);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 400);
  });

  QUnit.test('should apply default options', function(assert) {
    assert.expect(2);

    var done = assert.async();

    var callCount = 0,
        throttled = _.throttle(function() { callCount++; }, 32, {});

    throttled();
    throttled();
    assert.strictEqual(callCount, 1);

    setTimeout(function() {
      assert.strictEqual(callCount, 2);
      done();
    }, 128);
  });

  QUnit.test('should support a `leading` option', function(assert) {
    assert.expect(2);

    var withLeading = _.throttle(identity, 32, { 'leading': true });
    assert.strictEqual(withLeading('a'), 'a');

    var withoutLeading = _.throttle(identity, 32, { 'leading': false });
    assert.strictEqual(withoutLeading('a'), undefined);
  });

  QUnit.test('should support a `trailing` option', function(assert) {
    assert.expect(6);

    var done = assert.async();

    var withCount = 0,
        withoutCount = 0;

    var withTrailing = _.throttle(function(value) {
      withCount++;
      return value;
    }, 64, { 'trailing': true });

    var withoutTrailing = _.throttle(function(value) {
      withoutCount++;
      return value;
    }, 64, { 'trailing': false });

    assert.strictEqual(withTrailing('a'), 'a');
    assert.strictEqual(withTrailing('b'), 'a');

    assert.strictEqual(withoutTrailing('a'), 'a');
    assert.strictEqual(withoutTrailing('b'), 'a');

    setTimeout(function() {
      assert.strictEqual(withCount, 2);
      assert.strictEqual(withoutCount, 1);
      done();
    }, 256);
  });

  QUnit.test('should not update `lastCalled`, at the end of the timeout, when `trailing` is `false`', function(assert) {
    assert.expect(1);

    var done = assert.async();

    var callCount = 0;

    var throttled = _.throttle(function() {
      callCount++;
    }, 64, { 'trailing': false });

    throttled();
    throttled();

    setTimeout(function() {
      throttled();
      throttled();
    }, 96);

    setTimeout(function() {
      assert.ok(callCount > 1);
      done();
    }, 192);
  });

  QUnit.test('should work with a system time of `0`', function(assert) {
    assert.expect(3);

    var done = assert.async();

    if (!isModularize) {
      var callCount = 0,
          dateCount = 0;

      var lodash = _.runInContext({
        'Date': {
          'now': function() {
            return ++dateCount < 4 ? 0 : +new Date;
          }
        }
      });

      var throttled = lodash.throttle(function(value) {
        callCount++;
        return value;
      }, 32);

      var results = [throttled('a'), throttled('b'), throttled('c')];
      assert.deepEqual(results, ['a', 'a', 'a']);
      assert.strictEqual(callCount, 1);

      setTimeout(function() {
        assert.strictEqual(callCount, 2);
        done();
      }, 64);
    }
    else {
      skipAssert(assert, 3);
      done();
    }
  });
}());