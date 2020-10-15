(() => {
  process.argv.slice(2).reverse().reduce((f, filename) => {
    return () => {
      console.log(`Loading ${filename}...`);
      require('./' + filename);
      f();
    }
  }, () => {})();
})();
