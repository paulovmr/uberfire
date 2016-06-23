var editor = new wysihtml.Editor($("[data-field='html-editor']").get(0), {
    toolbar : document.getElementById('toolbar'),
    parserRules : wysihtmlParserRules
});

