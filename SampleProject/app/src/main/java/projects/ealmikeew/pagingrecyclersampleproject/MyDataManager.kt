package projects.ealmikeew.pagingrecyclersampleproject

data class MyModel(var title: String, var description: String)

object MyDataManager {
    // list of models
    private var modelList: ArrayList<MyModel?> = ArrayList()
    // search condition
    private var searchText: String? = null

    // sets up the condition
    fun updateSearchText(newSearchText: String?) {
        searchText = newSearchText
        modelList.clear()
    }

    fun myDataLoader(
        pageNumber: Int,
        pageSize: Int,
        preExecuteCallback: (() -> Unit)?,
        postExecuteCallback: () -> Unit
    ) {
        // makes up filter
        // passes PagingRecyclerView's preExecuteCallback
        // defines a postExecuteCallback which calls PagingRecyclerView's postExecuteCallback
        MyDataDownloader.download(
            MyDataDownloader.Filter(pageNumber, pageSize, searchText),
            preExecuteCallback,
            { downloadedData ->
                modelList.addAll(downloadedData)
                postExecuteCallback()
            })
    }

    // returns list of models
    fun myDataProvider(): ArrayList<MyModel?> {
        return modelList
    }
}