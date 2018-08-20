
    /**
     * @caseName@
     * @author @owner@
     */
    @Test(
        description="@caseName@",
        timeOut=@timeout@
    )
    @CaseOwner(description="@owner@")
    public void @method@() throws Throwable {
         Logger.info("execute case [@caseId@] by function [@method@]");
         executor.run("@caseId@");
    }
