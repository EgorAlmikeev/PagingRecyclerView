package projects.ealmikeew.pagingrecyclersampleproject

import android.os.AsyncTask

object MyDataDownloader {

    // download filter
    class Filter(var pageNumber: Int, var pageSize: Int, var searchText: String?)

    // downloads data asynchronously
    fun download(
        filter: Filter,
        preExecuteCallback: (() -> Unit)?,
        postExecuteCallback: (downloadedData: ArrayList<MyModel>) -> Unit
    ) {
        AsyncDataDownloader(preExecuteCallback, postExecuteCallback).execute(filter)
    }

    private class AsyncDataDownloader(
        private val preExecuteCallback: (() -> Unit)?,
        private val postExecuteCallback: (ArrayList<MyModel>) -> Unit
    ) : AsyncTask<Filter, Unit, ArrayList<MyModel>>() {
        // invokes MyDataManager's preExecuteCallback
        override fun onPreExecute() {
            super.onPreExecute()
            preExecuteCallback?.invoke()
        }

        override fun doInBackground(vararg params: Filter): ArrayList<MyModel> {
            Thread.sleep(1000)

            val pageNumber = params[0].pageNumber
            val pageSize = params[0].pageSize
            val searchText = params[0].searchText

            val result: ArrayList<MyModel> = ArrayList()

            // here you should get your new portion of data from Web API or generate in like here
            for (i in 0..pageSize) {
                if (searchText == null)
                    result.add(MyModel("MyModel $i", "Page $pageNumber"))
                else
                    result.add(MyModel("MyModel $searchText", "Page $pageNumber"))
            }

            return result
        }

        // invokes MyDataManager's postExecuteCallback
        override fun onPostExecute(result: ArrayList<MyModel>) {
            super.onPostExecute(result)
            postExecuteCallback(result)
        }
    }
}