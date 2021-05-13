QUnit.module('lodash.defaultsDeep');

(function() {
  QUnit.test('should deep assign source properties if missing on `object`', function(assert) {
    assert.expect(1);

    var object = { 'a': { 'b': 2 }, 'd': 4 },
        source = { 'a': { 'b': 3, 'c': 3 }, 'e': 5 },
        expected = { 'a': { 'b': 2, 'c': 3 }, 'd': 4, 'e': 5 };

    assert.deepEqual(_.defaultsDeep(object, source), expected);
  });

  QUnit.test('should accept multiple sources', function(assert) {
    assert.expect(2);

    var source1 = { 'a': { 'b': 3 } },
        source2 = { 'a': { 'c': 3 } },
        source3 = { 'a': { 'b': 3, 'c': 3 } },
        source4 = { 'a': { 'c': 4 } },
        expected = { 'a': { 'b': 2, 'c': 3 } };

    assert.deepEqual(_.defaultsDeep({ 'a': { 'b': 2 } }, source1, source2), expected);
    assert.deepEqual(_.defaultsDeep({ 'a': { 'b': 2 } }, source3, source4), expected);
  });

  QUnit.test('should not overwrite `null` values', function(assert) {
    assert.expect(1);

    var object = { 'a': { 'b': null } },
        source = { 'a': { 'b': 2 } },
        actual = _.defaultsDeep(object, source);

    assert.strictEqual(actual.a.b, null);
  });

  QUnit.test('should not overwrite regexp values', function(assert) {
    assert.expect(1);

    var object = { 'a': { 'b': /x/ } },
        source = { 'a': { 'b': /y/ } },
        actual = _.defaultsDeep(object, source);

    assert.deepEqual(actual.a.b, /x/);
  });

  QUnit.test('should not convert function properties to objects', function(assert) {
    assert.expect(2);

    var actual = _.defaultsDeep({}, { 'a': noop });
    assert.strictEqual(actual.a, noop);

    actual = _.defaultsDeep({}, { 'a': { 'b': noop } });
    assert.strictEqual(actual.a.b, noop);
  });

  QUnit.test('should overwrite `undefined` values', function(assert) {
    assert.expect(1);

    var object = { 'a': { 'b': undefined } },
        source = { 'a': { 'b': 2 } },
        actual = _.defaultsDeep(object, source);

    assert.strictEqual(actual.a.b, 2);
  });

  QUnit.test('should assign `undefined` values', function(assert) {
    assert.expect(1);

    var source = { 'a': undefined, 'b': { 'c': undefined, 'd': 1 } },
        expected = lodashStable.cloneDeep(source),
        actual = _.defaultsDeep({}, source);

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should merge sources containing circular references', function(assert) {
    assert.expect(2);

    var object = {
      'foo': { 'b': { 'c': { 'd': {} } } },
      'bar': { 'a': 2 }
    };

    var source = {
      'foo': { 'b': { 'c': { 'd': {} } } },
      'bar': {}
    };

    object.foo.b.c.d = object;
    source.foo.b.c.d = source;
    source.bar.b = source.foo.b;

    var actual = _.defaultsDeep(object, source);

    assert.strictEqual(actual.bar.b, actual.foo.b);
    assert.strictEqual(actual.foo.b.c.d, actual.foo.b.c.d.foo.b.c.d);
  });

  QUnit.test('should not modify sources', function(assert) {
    assert.expect(3);

    var source1 = { 'a': 1, 'b': { 'c': 2 } },
        source2 = { 'b': { 'c': 3, 'd': 3 } },
        actual = _.defaultsDeep({}, source1, source2);

    assert.deepEqual(actual, { 'a': 1, 'b': { 'c': 2, 'd': 3 } });
    assert.deepEqual(source1, { 'a': 1, 'b': { 'c': 2 } });
    assert.deepEqual(source2, { 'b': { 'c': 3, 'd': 3 } });
  });

  QUnit.test('should not attempt a merge of a string into an array', function(assert) {
    assert.expect(1);

    var actual = _.defaultsDeep({ 'a': ['abc'] }, { 'a': 'abc' });
    assert.deepEqual(actual.a, ['abc']);
  });

  QUnit.test('should not indirectly merge `Object` properties', function(assert) {
    assert.expect(1);

    _.defaultsDeep({}, { 'constructor': { 'a': 1 } });

    var actual = 'a' in Object;
    delete Object.a;

    assert.notOk(actual);
  });
}());