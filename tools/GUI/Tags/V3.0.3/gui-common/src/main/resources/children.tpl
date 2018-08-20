    public List<@class@> get@class@s()
    {
        ArrayList<@class@> arrayList = new ArrayList<@class@>();
        for(WebElement item : @children@)
        {
            arrayList.add(initPage(item,@class@.class));
        }
        return  arrayList;
    }