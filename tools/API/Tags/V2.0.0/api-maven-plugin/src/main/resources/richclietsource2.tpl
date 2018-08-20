
    /**
     * @caseName@
     * @author @owner@
     */
    @Test(
        description="@caseName@",
        dataProvider="@method@",
        groups={
            "@owner@",
            "Priority@priority@"
        },
        timeOut=@timeout@
    )
    public void @method@(Map<String, String> kvs) throws Exception {
        ApiUtils.addToContext("CASE_ID","@method@");
        Object result = verify.adapter(kvs);
        verify.verify(result);
    }

    @DataProvider(name="@method@")
    private Object[][] @method@DataProvider() throws Exception {
         return new Object[][]{