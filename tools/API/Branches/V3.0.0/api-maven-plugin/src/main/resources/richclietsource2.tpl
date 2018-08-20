
    /**
     * @caseName@
     * @author @owner@
     */
    @Test(
        description="@caseName@",
        dataProvider="@method@",
        timeOut=@timeout@
    )
    @CaseOwner(description="@owner@")
    public void @method@(Map<String, String> kvs) throws Exception {
        ContextUtils.addToContext("CASE_ID","@method@");
        Object result = verify.adapter(kvs);
        verify.verify(result);
    }

    @DataProvider(name="@method@")
    private Object[][] @method@DataProvider() throws Exception {
         return new Object[][]{