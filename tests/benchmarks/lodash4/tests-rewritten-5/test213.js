QUnit.module('lodash.runInContext');
(function () {
    QUnit.test('should not require a fully populated `context` object', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var lodash = _.runInContext({
                'setTimeout': function (func) {
                    func();
                }
            });
            var pass = __bool_top__;
            lodash.delay(function () {
                pass = __bool_top__;
            }, __num_top__);
            assert.ok(pass);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should use a zeroed `_.uniqueId` counter', function (assert) {
        assert.expect(3);
        if (!isModularize) {
            lodashStable.times(__num_top__, _.uniqueId);
            var oldId = Number(_.uniqueId()), lodash = _.runInContext();
            assert.ok(_.uniqueId() > oldId);
            var id = lodash.uniqueId();
            assert.strictEqual(id, __str_top__);
            assert.ok(id < oldId);
        } else {
            skipAssert(assert, 3);
        }
    });
}());