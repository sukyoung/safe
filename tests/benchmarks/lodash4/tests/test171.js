QUnit.module('lodash.omit');

(function() {
  var args = toArgs(['a', 'c']),
      object = { 'a': 1, 'b': 2, 'c': 3, 'd': 4 },
      nested = { 'a': 1, 'b': { 'c': 2, 'd': 3 } };

  QUnit.test('should flatten `paths`', function(assert) {
    assert.expect(2);

    assert.deepEqual(_.omit(object, 'a', 'c'), { 'b': 2, 'd': 4 });
    assert.deepEqual(_.omit(object, ['a', 'd'], 'c'), { 'b': 2 });
  });

  QUnit.test('should support deep paths', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.omit(nested, 'b.c'), { 'a': 1, 'b': { 'd': 3} });
  });

  QUnit.test('should support path arrays', function(assert) {
    assert.expect(1);

    var object = { 'a.b': 1, 'a': { 'b': 2 } },
        actual = _.omit(object, [['a.b']]);

    assert.deepEqual(actual, { 'a': { 'b': 2 } });
  });

  QUnit.test('should omit a key over a path', function(assert) {
    assert.expect(2);

    var object = { 'a.b': 1, 'a': { 'b': 2 } };

    lodashStable.each(['a.b', ['a.b']], function(path) {
      assert.deepEqual(_.omit(object, path), { 'a': { 'b': 2 } });
    });
  });

  QUnit.test('should coerce `paths` to strings', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.omit({ '0': 'a' }, 0), {});
  });

  QUnit.test('should return an empty object when `object` is nullish', function(assert) {
    assert.expect(2);

    lodashStable.each([null, undefined], function(value) {
      objectProto.a = 1;
      var actual = _.omit(value, 'valueOf');
      delete objectProto.a;
      assert.deepEqual(actual, {});
    });
  });

  QUnit.test('should work with a primitive `object`', function(assert) {
    assert.expect(1);

    stringProto.a = 1;
    stringProto.b = 2;

    assert.deepEqual(_.omit('', 'b'), { 'a': 1 });

    delete stringProto.a;
    delete stringProto.b;
  });

  QUnit.test('should work with `arguments` object `paths`', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.omit(object, args), { 'b': 2, 'd': 4 });
  });

  QUnit.test('should not mutate `object`', function(assert) {
    assert.expect(4);

    lodashStable.each(['a', ['a'], 'a.b', ['a.b']], function(path) {
      var object = { 'a': { 'b': 2 } };
      _.omit(object, path);
      assert.deepEqual(object, { 'a': { 'b': 2 } });
    });
  });
}());