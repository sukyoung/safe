QUnit.module('strict mode checks');

lodashStable.each(['assign', 'assignIn', 'bindAll', 'defaults', 'defaultsDeep', 'merge'], function(methodName) {
  var func = _[methodName],
      isBindAll = methodName == 'bindAll';

  QUnit.test('`_.' + methodName + '` should ' + (isStrict ? '' : 'not ') + 'throw strict mode errors', function(assert) {
    assert.expect(1);

    var object = freeze({ 'a': undefined, 'b': function() {} }),
        pass = !isStrict;

    try {
      func(object, isBindAll ? 'b' : { 'a': 1 });
    } catch (e) {
      pass = !pass;
    }
    assert.ok(pass);
  });
});