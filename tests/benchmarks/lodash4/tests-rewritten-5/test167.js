QUnit.module('lodash.noConflict');
(function () {
    QUnit.test('should return the `lodash` function', function (assert) {
        assert.expect(2);
        if (!isModularize) {
            assert.strictEqual(_.noConflict(), oldDash);
            assert.notStrictEqual(root._, oldDash);
            root._ = oldDash;
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should restore `_` only if `lodash` is the current `_` value', function (assert) {
        assert.expect(2);
        if (!isModularize) {
            var _ = root._;
            var object = root._ = {};
            assert.strictEqual(_.noConflict(), oldDash);
            assert.strictEqual(root._, object);
            root._ = oldDash;
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should work with a `root` of `this`', function (assert) {
        assert.expect(2);
        if (!coverage && !document && !isModularize && realm.object) {
            var fs = require(__str_top__), vm = require(__str_top__), expected = {}, context = vm.createContext({
                    '_': expected,
                    'console': console
                }), source = fs.readFileSync(filePath, __str_top__);
            vm.runInContext(source + __str_top__, context);
            assert.strictEqual(context._, expected);
            assert.ok(context.lodash);
        } else {
            skipAssert(assert, 2);
        }
    });
}());